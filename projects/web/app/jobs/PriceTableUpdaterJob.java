package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;

import app.client.PricingApi;
import app.common.utils.DateCompareUtil;
import app.models.Body;
import app.models.PluginPriceModel;
import app.models.PluginPriceTableModel;
import app.models.PluginPriceTableStatusModel;
import app.models.PluginPriceTableStatusModel.Status;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;
import models.InstanceTypeModel;
import models.InstanceTypeRegionModel;
import models.PluginModel;
import models.PriceTableModel;
import models.PriceTableModel.SyncStatus;
import models.RegionModel;
import models.StorageRegionModel;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
@Every("30min")
public class PriceTableUpdaterJob extends Job {

    private static Lock _lock_ = new ReentrantLock();

    @Override
    public void doJob() {
        processPlugins();
    }

    public static boolean processPlugins() {
        if (_lock_.tryLock()) {
            try {
                final List<PluginModel> listPlugins = PluginModel.findAll();
                for (final PluginModel plugin : listPlugins) {
                    processPlugin(plugin);
                }
            } finally {
                _lock_.unlock();
            }
            return true;
        }
        return false;
    }

    private static void processPlugin(final PluginModel plugin) {
        final PriceTableModel recentPriceTable = plugin.getPriceTable();
        final Date now = new Date();
        try {
            final PricingApi api = new PricingApi(plugin.getUrl());
            final Body<PluginPriceTableModel> price = api.getPricing();
            final PluginPriceModel princingRequested = price.getContent().getPrice();
            final PluginPriceTableStatusModel statusRequested = price.getContent().getStatus();
            if (!priceTableMustBeSync(recentPriceTable, princingRequested, statusRequested)) {
                updateOrCreatePriceTableStatus(
                        plugin, recentPriceTable, now,
                        SyncStatus.OK, "OK", null, null);
            } else {
                processPriceTable(now, princingRequested, statusRequested, plugin);
            }
        } catch (final Exception e) {
            Logger.error(e, "Price table for plugin [%s] cannot be retrieved [%s]", plugin.getName(), e.getMessage());                
            String message = e.getMessage() == null && e.getCause() != null ? e.getCause().getMessage() : null;
            message = StringUtils.isNotEmpty(message) ? message : Messages.get("application.unknow.error.synchronizing.process");
            updateOrCreatePriceTableStatus(
                    plugin, recentPriceTable, now,
                    SyncStatus.ERROR, message, null, null);
        }
    }

    private static void processPriceTable(
            final Date now,
            final PluginPriceModel princingRequested,
            final PluginPriceTableStatusModel princingStatusRequested,
            final PluginModel plugin) {

        // Clean current price table
        PriceTableModel priceTable = plugin.getPriceTable();
        if (priceTable != null) {
            PriceTableModel.deletePriceTable(priceTable.getId());
            priceTable = null;
        }

        priceTable = updateOrCreatePriceTableStatus(
                plugin, null, now,
                PriceTableModel.getStatus(princingStatusRequested),
                princingStatusRequested.getErrorMessage(),
                princingRequested,
                princingStatusRequested);

        createStoragePrices(princingRequested, priceTable);
        createInstancePrices(princingRequested, priceTable);
    }

    private static void createStoragePrices(
            final PluginPriceModel princingRequested,
            final PriceTableModel priceTable) {
        for (final Map.Entry<String, StoragePricing> entryInstance : princingRequested.getListStoragePricing().entrySet()) {

            final StoragePricing storagePrice = entryInstance.getValue();

            final RegionModel region = new RegionModel();
            region.setName(storagePrice.getRegion());
            region.save();

            final StorageRegionModel storageRegion = new StorageRegionModel(region);
            storageRegion.setPrice(storagePrice.getPrice());
            storageRegion.setClassAPrice(storagePrice.getClassAPrice());
            storageRegion.setClassBPrice(storagePrice.getClassBPrice());
            storageRegion.setPriceTable(priceTable);
            storageRegion.save();

        }
    }

    private static void createInstancePrices(
            final PluginPriceModel princingRequested,
            final PriceTableModel priceTable) {
        final HashMap<String, RegionModel> listRegions = new HashMap<>();
        for (final Map.Entry<String, InstanceTypePricing> entryInstance : princingRequested.getListInstancePricing().entrySet()) {

            final InstanceTypePricing instancePrice = entryInstance.getValue();

            final InstanceTypeModel instanceType = new InstanceTypeModel();
            instanceType.setMemory(instancePrice.getMemory());
            instanceType.setCores(instancePrice.getCores());
            instanceType.setName(instancePrice.getName());
            instanceType.save();

            for (final Map.Entry<String, Double> entryRegion : instancePrice.getListRegionPricing().entrySet()) {

                final String regionKey = entryRegion.getKey();
                final Double regionPrice = entryRegion.getValue();
                RegionModel region = null;
                if (listRegions.containsKey(regionKey)) {
                    region = listRegions.get(regionKey);
                } else {
                    region = new RegionModel();
                    region.setName(regionKey);
                    region.save();
                    listRegions.put(regionKey, region);
                }

                final InstanceTypeRegionModel instancePriceRegion = new InstanceTypeRegionModel(instanceType, region);
                instancePriceRegion.setPrice(regionPrice);
                instancePriceRegion.setPriceTable(priceTable);
                instancePriceRegion.save();
            }
        }
    }

    private static PriceTableModel updateOrCreatePriceTableStatus(
            final PluginModel plugin,
            PriceTableModel priceTable,
            final Date now,
            final SyncStatus status,
            final String messageError,
            final PluginPriceModel princingRequested,
            final PluginPriceTableStatusModel princingStatusRequested) {

        priceTable = PriceTableModel.findByPluginId(plugin.getId());
        if (priceTable == null) {
            priceTable = new PriceTableModel();
            priceTable.setPlugin(plugin);
        }
        priceTable.setLastSyncDate(now);
        priceTable.setSyncStatus(status);
        priceTable.setSyncMessage(messageError);

        if (princingRequested != null) {
            priceTable.setPriceTableDate(princingRequested.getLastUpdate());
            priceTable.setLastSearchDate(princingStatusRequested.getLastSearch());
        }

        priceTable.save();
        return priceTable;
    }

    private static boolean priceTableMustBeSync(
            final PriceTableModel recentPriceTable,
            final PluginPriceModel princingRequested,
            final PluginPriceTableStatusModel princingStatusRequested) {
        if (recentPriceTable == null || recentPriceTable.getPriceTableDate() == null) {
            return true;
        }
        if (DateCompareUtil.is(princingRequested.getLastUpdate())
                .greaterThan(recentPriceTable.getPriceTableDate())) {
            return true;
        }
        if (princingStatusRequested.getStatus() != Status.OK) {
            return true;
        }
        return false;
    }
}
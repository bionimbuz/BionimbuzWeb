package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        if(_lock_.tryLock()) {
            try {
                List<PluginModel> listPlugins =
                        PluginModel.findAll();
                for(PluginModel plugin : listPlugins) {
                    processPlugin(plugin);
                }
            }
            finally {
                _lock_.unlock();
            }
            return true;
        }
        return false;
    }

    private static void processPlugin(final PluginModel plugin) {
        PriceTableModel recentPriceTable = plugin.getPriceTable();
        Date now = new Date();
        try {
            PricingApi api = new PricingApi(plugin.getUrl());
            Body<PluginPriceTableModel> price =
                    api.getPricing();
            PluginPriceModel princingRequested = price.getContent().getPrice();
            PluginPriceTableStatusModel statusRequested = price.getContent().getStatus();
            if(!priceTableMustBeSync(recentPriceTable, princingRequested, statusRequested)){
                updateOrCreatePriceTableStatus(
                        plugin, recentPriceTable, now,
                        SyncStatus.OK, "OK", null, null);
            }
            else {
                processPriceTable(now, princingRequested, statusRequested, plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateOrCreatePriceTableStatus(
                    plugin, recentPriceTable, now,
                    SyncStatus.ERROR, e.getMessage(), null, null);
        }
    }

    private static void processPriceTable(
            final Date now,
            final PluginPriceModel princingRequested,
            final PluginPriceTableStatusModel princingStatusRequested,
            final PluginModel plugin) {

        // Clean current price table
        PriceTableModel priceTable = plugin.getPriceTable();
        if(priceTable != null) {
            PriceTableModel.deletePriceTable(priceTable.getId());
        }

        priceTable =
                updateOrCreatePriceTableStatus(
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
        for(Map.Entry<String, StoragePricing> entryInstance :
                princingRequested.getListStoragePricing().entrySet()) {

            StoragePricing storagePrice = entryInstance.getValue();

            RegionModel region = new RegionModel();
            region.setName(storagePrice.getRegion());
            region.save();

            StorageRegionModel storageRegion =
                    new StorageRegionModel(region);
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
        HashMap<String, RegionModel> listRegions = new HashMap<>();
        for(Map.Entry<String, InstanceTypePricing> entryInstance :
                princingRequested.getListInstancePricing().entrySet()) {

            InstanceTypePricing instancePrice = entryInstance.getValue();

            InstanceTypeModel instanceType = new InstanceTypeModel();
            instanceType.setMemory(instancePrice.getMemory());
            instanceType.setCores(instancePrice.getCores());
            instanceType.setName(instancePrice.getName());
            instanceType.save();

            for(Map.Entry<String, Double> entryRegion :
                    instancePrice.getListRegionPricing().entrySet()) {

                String regionKey = entryRegion.getKey();
                Double regionPrice = entryRegion.getValue();
                RegionModel region = null;
                if(listRegions.containsKey(regionKey)) {
                    region = listRegions.get(regionKey);
                } else {
                    region = new RegionModel();
                    region.setName(regionKey);
                    region.save();
                    listRegions.put(regionKey, region);
                }

                InstanceTypeRegionModel instancePriceRegion =
                        new InstanceTypeRegionModel(instanceType, region);
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

        if(priceTable == null) {
            priceTable = new PriceTableModel();
            priceTable.setPlugin(plugin);
        }
        priceTable.setLastSyncDate(now);
        priceTable.setSyncStatus(status);
        priceTable.setSyncMessage(messageError);

        if(princingRequested != null) {
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
        if(recentPriceTable == null) {
            return true;
        }
        if(DateCompareUtil.is(princingRequested.getLastUpdate())
                .greaterThan(recentPriceTable.getPriceTableDate())) {
            return true;
        }
        if(princingStatusRequested.getStatus() != Status.OK) {
            return true;
        }
        return false;
    }
}
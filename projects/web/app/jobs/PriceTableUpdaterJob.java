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
import app.models.PriceModel;
import app.models.PriceTableStatusModel;
import app.models.PriceTableStatusModel.Status;
import app.models.pricing.InstanceTypePricing;
import app.models.pricing.ZonePricing;
import models.InstanceTypeModel;
import models.InstanceTypeZoneModel;
import models.PluginModel;
import models.PriceTableModel;
import models.PriceTableModel.SyncStatus;
import models.ZoneModel;
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
            Body<app.models.PriceTableModel> price = 
                    api.getPricing();  
            PriceModel princingRequested = price.getContent().getPrice();
            PriceTableStatusModel statusRequested = price.getContent().getStatus();
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

    private static PriceTableModel updateOrCreatePriceTableStatus(
            final PluginModel plugin,
            PriceTableModel priceTable, 
            final Date now, 
            final SyncStatus status, 
            final String messageError,
            final PriceModel princingRequested,
            final PriceTableStatusModel princingStatusRequested) {
        
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
    
    private static void processPriceTable(
            final Date now,
            final PriceModel princingRequested,
            final PriceTableStatusModel princingStatusRequested,
            final PluginModel plugin) {
        
        // Clean current price table
        PriceTableModel priceTable = plugin.getPriceTable();
        if(priceTable != null) {
            PriceTableModel.deletePriceTable(priceTable.getId());
        }

        HashMap<String, ZoneModel> listZones = new HashMap<>();  
        priceTable = 
                updateOrCreatePriceTableStatus(
                    plugin, null, now, 
                    PriceTableModel.getStatus(princingStatusRequested), 
                    princingStatusRequested.getErrorMessage(), 
                    princingRequested,
                    princingStatusRequested);

        for(Map.Entry<String, InstanceTypePricing> entryInstance : 
                princingRequested.getListInstancePricing().entrySet()) {
            
            InstanceTypePricing instancePrice = entryInstance.getValue();
            
            InstanceTypeModel instanceType = new InstanceTypeModel();
            instanceType.setMemory(instancePrice.getMemory());
            instanceType.setCores(instancePrice.getCores());
            instanceType.setName(instancePrice.getName());
            instanceType.save();

            for(Map.Entry<String, ZonePricing> entryZone : 
                    instancePrice.getListZonePricing().entrySet()) {

                String zoneKey = entryZone.getKey();
                ZonePricing zonePrice = entryZone.getValue();
                ZoneModel zone = null;
                if(listZones.containsKey(zoneKey)) {
                    zone = listZones.get(zoneKey);
                } else {
                    zone = new ZoneModel();
                    zone.setName(zoneKey);
                    zone.save();
                    listZones.put(zoneKey, zone);
                }
                
                InstanceTypeZoneModel instancePriceZone = 
                        new InstanceTypeZoneModel(instanceType, zone);
                instancePriceZone.setPrice(zonePrice.getPrice());
                instancePriceZone.setPriceTable(priceTable);
                instancePriceZone.save();
            }
        }
    }
    
    private static boolean priceTableMustBeSync(
            final PriceTableModel recentPriceTable,
            final PriceModel princingRequested,
            final PriceTableStatusModel princingStatusRequested) {
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
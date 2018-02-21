package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import app.client.PricingApi;
import app.models.Body;
import app.models.PricingModel;
import app.models.PricingStatusModel;
import app.models.PricingStatusModel.Status;
import app.models.pricing.InstancePricing;
import app.models.pricing.ZonePricing;
import common.utils.DateUtil;
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
@Every("1min")
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
            Body<PricingModel> price = 
                    api.getPricing();  
            PricingModel princingRequested = price.getContent();
            if(!priceTableMustBeSync(recentPriceTable, princingRequested)){
                updateOrCreatePriceTableStatus(
                        plugin, recentPriceTable, now, 
                        SyncStatus.OK, "OK", null);
            }
            else {
                processPriceTable(now, princingRequested, plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateOrCreatePriceTableStatus(
                    plugin, recentPriceTable, now, 
                    SyncStatus.ERROR, e.getMessage(), null);
        }
    }

    private static PriceTableModel updateOrCreatePriceTableStatus(
            final PluginModel plugin,
            PriceTableModel priceTable, 
            final Date now, 
            final SyncStatus status, 
            final String messageError,
            final PricingModel princingRequested) {
        
        if(priceTable == null) {
            priceTable = new PriceTableModel();
            priceTable.setPlugin(plugin);
        }
        priceTable.setLastSyncDate(now);
        priceTable.setSyncStatus(status);
        priceTable.setSyncMessage(messageError);
        
        if(princingRequested != null) {

            priceTable.setPriceTableDate(princingRequested.getLastUpdate());
            priceTable.setLastSearchDate(princingRequested.getStatus().getLastSearch());
        }
        
        priceTable.save();
        return priceTable;
    }
    
    private static void processPriceTable(
            final Date now,
            final PricingModel princingRequested,
            final PluginModel plugin) {

        HashMap<String, ZoneModel> listZones = new HashMap<>();  
        PricingStatusModel status = princingRequested.getStatus();
        PriceTableModel priceTable = 
                updateOrCreatePriceTableStatus(
                    plugin, null, now, 
                    PriceTableModel.getStatus(status), 
                    status.getErrorMessage(), 
                    princingRequested);

        for(Map.Entry<String, InstancePricing> entryInstance : 
                princingRequested.getListInstancePricing().entrySet()) {
            
            InstancePricing instancePrice = entryInstance.getValue();
            
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
            final PricingModel princingRequested) {
        if(recentPriceTable == null) {
            return true;
        }        
        if(DateUtil.is(princingRequested.getLastUpdate())
                .greaterThan(recentPriceTable.getPriceTableDate())) {
            return true;
        }
        if(princingRequested.getStatus().getStatus() != Status.OK) {
            return true;
        }
        return false;        
    }
}
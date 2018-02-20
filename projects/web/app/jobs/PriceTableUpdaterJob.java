package jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.client.PricingApi;
import app.models.Body;
import app.models.PricingModel;
import app.models.pricing.InstancePricing;
import app.models.pricing.ZonePricing;
import common.utils.DateUtil;
import models.InstanceTypeModel;
import models.InstanceTypeZoneModel;
import models.PluginModel;
import models.PriceTableModel;
import models.ZoneModel;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;


@OnApplicationStart
@Every("1min")
public class PriceTableUpdaterJob extends Job {

    @Override
    public void doJob() {
        
        List<PluginModel> listPlugins = 
                PluginModel.findAll();
        
        for(PluginModel plugin : listPlugins) {
            try {
                PriceTableModel recentPriceTable = 
                        PriceTableModel.getRecentPriceTable(plugin.getId());
                PricingApi api = new PricingApi(plugin.getUrl());   
                Body<PricingModel> price = 
                        api.getPricing();  
                Date now = new Date(); 
                
                if(!priceTableMustBeSync(recentPriceTable, price.getContent())){
                    recentPriceTable.setLastSyncDate(now);
                    recentPriceTable.save();
                    continue;
                }
                else {
                    processPriceTable(now, price.getContent(), plugin);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void processPriceTable(
            final Date now,
            final PricingModel princingRequested,
            final PluginModel plugin) {

        HashMap<String, ZoneModel> listZones = new HashMap<>();  
        
        PriceTableModel priceTable = new PriceTableModel();
        priceTable.setLastSearchDate(princingRequested.getStatus().getLastSearch());
        priceTable.setLastSyncDate(now);
        priceTable.setPriceTableDate(princingRequested.getLastUpdate());
        priceTable.setPlugin(plugin);        
        priceTable.save();

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
    
    private boolean priceTableMustBeSync(
            final PriceTableModel recentPriceTable,
            final PricingModel princingRequested) {
        if(recentPriceTable == null) {
            return true;
        }        
        if(DateUtil.is(princingRequested.getLastUpdate())
                .greaterThan(recentPriceTable.getPriceTableDate())) {
            return true;
        }
        return false;        
    }
}
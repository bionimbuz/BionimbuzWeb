package jobs;

import java.io.IOException;
import java.util.List;

import app.client.PricingApi;
import app.models.Body;
import app.models.PricingModel;
import models.PluginModel;
import play.jobs.Job;

//@Every("1h")
public class PriceTableUpdaterJob extends Job {

    public void doJob() {
        
        List<PluginModel> listPlugins = 
                PluginModel.findAll();
        
        for(PluginModel plugin : listPlugins) {
            try {
                PricingApi api = new PricingApi(plugin.getUrl());   
                Body<PricingModel> price = 
                        api.getPricing();                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
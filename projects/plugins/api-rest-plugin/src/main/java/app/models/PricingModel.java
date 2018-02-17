package app.models;

import java.util.Date;
import java.util.HashMap;

import app.models.pricing.InstancePricing;

public class PricingModel extends Body {

    private Date lastUpdate;
    private Date lastSearch;
    private HashMap<String, InstancePricing> listInstancePricing;
    

    public PricingModel() {        
    }
    public PricingModel(
            final Date lastUpdate,
            final HashMap<String, InstancePricing> listInstancePricing) {
        super();
        this.lastUpdate = lastUpdate;
        this.listInstancePricing = listInstancePricing;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public HashMap<String, InstancePricing> getListInstancePricing() {
        return listInstancePricing;
    }
    public void setListInstancePricing(
            HashMap<String, InstancePricing> listInstancePricing) {
        this.listInstancePricing = listInstancePricing;
    }
    public Date getLastSearch() {
        return lastSearch;
    }
    public void setLastSearch(Date lastSearch) {
        this.lastSearch = lastSearch;
    }
}

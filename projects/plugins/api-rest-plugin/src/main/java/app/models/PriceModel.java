package app.models;

import java.util.Date;
import java.util.HashMap;

import app.models.pricing.InstanceTypePricing;

public class PriceModel extends Body {

    private Date lastUpdate;
    private HashMap<String, InstanceTypePricing> listInstancePricing;
    
    public PriceModel(
            final Date lastUpdate,
            final HashMap<String, InstanceTypePricing> listInstancePricing) {
        super();
        this.lastUpdate = lastUpdate;
        this.listInstancePricing = listInstancePricing;
    }
    public PriceModel() {
        this(null, null);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
    public HashMap<String, InstanceTypePricing> getListInstancePricing() {
        return listInstancePricing;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public void setListInstancePricing(
            HashMap<String, InstanceTypePricing> listInstancePricing) {
        this.listInstancePricing = listInstancePricing;
    }
}

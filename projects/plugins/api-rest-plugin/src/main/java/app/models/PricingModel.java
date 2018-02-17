package app.models;

import java.util.Date;
import java.util.HashMap;

import app.models.pricing.InstancePricing;
import app.models.pricing.StatusPricing;

public class PricingModel extends Body {

    private Date lastUpdate;
    private HashMap<String, InstancePricing> listInstancePricing;
    private StatusPricing status;    
    
    public PricingModel(
            final StatusPricing status,
            final Date lastUpdate,
            final HashMap<String, InstancePricing> listInstancePricing) {
        super();
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.listInstancePricing = listInstancePricing;
    }
    public PricingModel(
            final StatusPricing status) {
        this(status, null, null);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
    public HashMap<String, InstancePricing> getListInstancePricing() {
        return listInstancePricing;
    }
    public StatusPricing getStatus() {
        return status;
    }
    public void setStatus(StatusPricing status) {
        this.status = status;
    }
    
}

package app.models;

import java.util.Date;
import java.util.HashMap;

import app.models.pricing.InstancePricing;

public class PricingModel extends Body {

    private Date lastUpdate;
    private HashMap<String, InstancePricing> listInstancePricing;
    private PricingStatusModel status;    
    
    public PricingModel(
            final PricingStatusModel status,
            final Date lastUpdate,
            final HashMap<String, InstancePricing> listInstancePricing) {
        super();
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.listInstancePricing = listInstancePricing;
    }
    public PricingModel(
            final PricingStatusModel status) {
        this(status, null, null);
    }
    public PricingModel() {
        this(PricingStatusModel.createProcessingStatus(), null, null);
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
    public HashMap<String, InstancePricing> getListInstancePricing() {
        return listInstancePricing;
    }
    public PricingStatusModel getStatus() {
        return status;
    }
    public void setStatus(PricingStatusModel status) {
        this.status = status;
    }
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public void setListInstancePricing(
            HashMap<String, InstancePricing> listInstancePricing) {
        this.listInstancePricing = listInstancePricing;
    }
}

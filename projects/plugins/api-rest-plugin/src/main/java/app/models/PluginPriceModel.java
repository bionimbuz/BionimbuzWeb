package app.models;

import java.util.Date;
import java.util.HashMap;

import app.models.pricing.InstanceTypePricing;
import app.models.pricing.StoragePricing;

public class PluginPriceModel extends Body {

    private Date lastUpdate;
    private HashMap<String, InstanceTypePricing> listInstancePricing;
    private HashMap<String, StoragePricing> listStoragePricing;

    public PluginPriceModel(
            final Date lastUpdate,
            final HashMap<String, InstanceTypePricing> listInstancePricing,
            final HashMap<String, StoragePricing> listStoragePricing) {
        super();
        this.lastUpdate = lastUpdate;
        this.listInstancePricing = listInstancePricing;
        this.listStoragePricing = listStoragePricing;
    }
    public PluginPriceModel() {
        this(null, null, null);
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
    public HashMap<String, StoragePricing> getListStoragePricing() {
        return listStoragePricing;
    }
    public void setListStoragePricing(
            HashMap<String, StoragePricing> listStoragePricing) {
        this.listStoragePricing = listStoragePricing;
    }
}

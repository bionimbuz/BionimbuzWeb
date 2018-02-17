package app.models.pricing;

import java.util.HashMap;

public class InstancePricing {
    
    private String name;
    private Short cores;
    private Double memory;
    private HashMap<String, ZonePricing> listZonePricing;
    
    public InstancePricing(
            final String name, 
            final Short cores, 
            final Double memory,
            final HashMap<String, ZonePricing> listZonePricing) {
        this.name = name;
        this.cores = cores;
        this.memory = memory;
        this.listZonePricing = listZonePricing;
    }

    public InstancePricing() {        
    }
    
    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public Short getCores() {
        return cores;
    }
    public void setCores(final Short cores) {
        this.cores = cores;
    }
    public Double getMemory() {
        return memory;
    }
    public void setMemory(final Double memory) {
        this.memory = memory;
    }
    public HashMap<String, ZonePricing> getListZonePricing() {
        return listZonePricing;
    }
    public void setListZonePricing(final HashMap<String, ZonePricing> listZonePricing) {
        this.listZonePricing = listZonePricing;
    }
}

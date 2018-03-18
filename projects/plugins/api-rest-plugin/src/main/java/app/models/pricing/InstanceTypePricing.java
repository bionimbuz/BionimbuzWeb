package app.models.pricing;

import java.util.HashMap;

public class InstanceTypePricing {
    
    private String name;
    private Short cores;
    private Double memory;
    private HashMap<String, RegionPricing> listRegionPricing;
    
    public InstanceTypePricing(
            final String name, 
            final Short cores, 
            final Double memory,
            final HashMap<String, RegionPricing> listRegionPricing) {
        this.name = name;
        this.cores = cores;
        this.memory = memory;
        this.listRegionPricing = listRegionPricing;
    }

    public InstanceTypePricing() {        
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
    public HashMap<String, RegionPricing> getListRegionPricing() {
        return listRegionPricing;
    }
    public void setListRegionPricing(
            HashMap<String, RegionPricing> listRegionPricing) {
        this.listRegionPricing = listRegionPricing;
    }    
}
package app.models.pricing;

import java.util.HashMap;

public class InstanceTypePricing {

    private String name;
    private Short cores;
    private Double memory;
    private String flavorId;
    private HashMap<String, Double> listRegionPricing;

    public InstanceTypePricing(
            final String name,
            final Short cores,
            final Double memory) {
        this(name, cores, memory, new HashMap<String, Double>());
    }
    
    public InstanceTypePricing(
            final String name,
            final Short cores,
            final Double memory,
            final HashMap<String, Double> listRegionPricing) {
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
    public HashMap<String, Double> getListRegionPricing() {
        return listRegionPricing;
    }
    public void setListRegionPricing(HashMap<String, Double> listRegionPricing) {
        this.listRegionPricing = listRegionPricing;
    }
    public String getFlavorId() {
        return flavorId;
    }
    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId;
    }
}

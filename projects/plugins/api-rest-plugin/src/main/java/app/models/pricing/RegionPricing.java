package app.models.pricing;

public class RegionPricing {

    private Double price;
    private String name;
    
    public RegionPricing() {
    }
    
    public RegionPricing(final String name, final Double price) {
        this.price = price;
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(final Double price) {
        this.price = price;
    }
    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
}

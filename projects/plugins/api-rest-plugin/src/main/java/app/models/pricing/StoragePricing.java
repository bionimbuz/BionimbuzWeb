package app.models.pricing;

public class StoragePricing {

    private String region;
    private Double price;
    private Double classAPrice;
    private Double classBPrice;

    public StoragePricing(
            final String region, final Double price,
            final Double classAPrice, final Double classBPrice) {
        this.region = region;
        this.price = price;
        this.classAPrice = classAPrice;
        this.classBPrice = classBPrice;
    }

    public StoragePricing() {
        this(null, null, null, null);
    }

    public StoragePricing(final String location) {
        this(location, null, null, null);
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Double getClassAPrice() {
        return classAPrice;
    }
    public void setClassAPrice(Double classAPrice) {
        this.classAPrice = classAPrice;
    }
    public Double getClassBPrice() {
        return classBPrice;
    }
    public void setClassBPrice(Double classBPrice) {
        this.classBPrice = classBPrice;
    }
}

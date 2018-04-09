package app.models.pricing;

public class StoragePricing {

    private String location;
    private Double price;
    private Double classAPrice;
    private Double classBPrice;

    public StoragePricing(
            final String location, final Double price,
            final Double classAPrice, final Double classBPrice) {
        this.location = location;
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

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
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

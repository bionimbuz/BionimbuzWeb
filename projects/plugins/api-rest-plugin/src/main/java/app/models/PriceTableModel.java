package app.models;

public class PriceTableModel extends Body {

    private PriceTableStatusModel status;       
    private PriceModel price;
    
    public PriceTableModel(PriceTableStatusModel status, PriceModel price) {
        super();
        this.status = status;
        this.price = price;
    }
    public PriceTableModel() {        
    }

    public PriceTableStatusModel getStatus() {
        return status;
    }
    public void setStatus(PriceTableStatusModel status) {
        this.status = status;
    }
    public PriceModel getPrice() {
        return price;
    }
    public void setPrice(PriceModel price) {
        this.price = price;
    }
}

package app.models;

public class PluginPriceTableModel extends Body {

    private PluginPriceTableStatusModel status;       
    private PluginPriceModel price;
    
    public PluginPriceTableModel(PluginPriceTableStatusModel status, PluginPriceModel price) {
        super();
        this.status = status;
        this.price = price;
    }
    public PluginPriceTableModel() {        
    }

    public PluginPriceTableStatusModel getStatus() {
        return status;
    }
    public void setStatus(PluginPriceTableStatusModel status) {
        this.status = status;
    }
    public PluginPriceModel getPrice() {
        return price;
    }
    public void setPrice(PluginPriceModel price) {
        this.price = price;
    }
}

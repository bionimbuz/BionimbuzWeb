package app.models;

public class PluginComputingRegionModel extends Body {
    
    private String name;
        
    @SuppressWarnings("unused") //Reflection purposes
    private PluginComputingRegionModel() {    
        super();	
    }
    
    public PluginComputingRegionModel(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

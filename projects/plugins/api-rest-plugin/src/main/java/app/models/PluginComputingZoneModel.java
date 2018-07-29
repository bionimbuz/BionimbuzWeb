package app.models;

public class PluginComputingZoneModel extends Body {
    
    private String name;
        
    @SuppressWarnings("unused") //Reflection purposes
    private PluginComputingZoneModel() {    
        super();	
    }
    
    public PluginComputingZoneModel(String name) {
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

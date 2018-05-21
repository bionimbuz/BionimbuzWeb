package app.models;

public class PluginInstanceZoneModel extends Body {
    
    private String name;
        
    @SuppressWarnings("unused") //Reflection purposes
    private PluginInstanceZoneModel() {    
        super();	
    }
    
    public PluginInstanceZoneModel(String name) {
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

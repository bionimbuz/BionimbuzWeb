package app.models;

public class PluginZoneModel extends Body {
    
    private String name;
        
    @SuppressWarnings("unused") //Reflection purposes
    private PluginZoneModel() {    
        super();	
    }
    
    public PluginZoneModel(String name) {
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

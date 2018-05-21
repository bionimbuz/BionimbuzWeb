package app.models;

public class PluginInstanceRegionModel extends Body {
    
    private String name;
        
    @SuppressWarnings("unused") //Reflection purposes
    private PluginInstanceRegionModel() {    
        super();	
    }
    
    public PluginInstanceRegionModel(String name) {
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

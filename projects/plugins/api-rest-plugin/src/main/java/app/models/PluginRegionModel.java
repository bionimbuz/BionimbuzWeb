package app.models;

public class PluginRegionModel extends Body {
    
    private String name;
        
    @SuppressWarnings("unused") //Reflection purposes
    private PluginRegionModel() {    
        super();	
    }
    
    public PluginRegionModel(String name) {
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

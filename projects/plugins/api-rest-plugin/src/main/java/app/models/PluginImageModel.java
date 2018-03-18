package app.models;

public class PluginImageModel extends Body {

    private String id; 
    private String name; 
    private String url;
    
    public PluginImageModel() {
        super();
    } 
    public PluginImageModel(String id, String name, String url) {
        super();
        this.id = id;
        this.name = name;
        this.url = url;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
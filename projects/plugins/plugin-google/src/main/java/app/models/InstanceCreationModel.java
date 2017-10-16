package app.models;

import java.net.URI;

public class InstanceCreationModel {

    private String startupScript;
    private String region;
    private String zone;
    private String imageUrl;
    private String type;

    public String getStartupScript() {
        return startupScript;
    }
    public void setStartupScript(String startupScript) {
        this.startupScript = startupScript;
    }
    public String getZone() {
        return zone;
    }
    public void setZone(String zone) {
        this.zone = zone;
    }    
    public URI getImageUri() {
        return URI.create(imageUrl);
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}

package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import controllers.CRUD.Hidden;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_image")
public class ImageModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private PluginModel plugin;
    @Required
    @MaxSize(100)
    @Hidden
    private String name;        
    @Required
    @MaxSize(500)
    private String url;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "listImages")
    private List<ApplicationModel> listApplications;
    
    public ImageModel() {        
        super();
    }
    
    public ImageModel(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }    
    public PluginModel getPlugin() {
        return plugin;
    }
    public void setPlugin(PluginModel plugin) {
        this.plugin = plugin;
    }
    public List<ApplicationModel> getListApplications() {
        return listApplications;
    }
    public void setListApplications(List<ApplicationModel> listApplications) {
        this.listApplications = listApplications;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

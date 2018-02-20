package models;

import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Inheritance
@DiscriminatorColumn(name="type")
@Table(name = "tb_application")
public class ApplicationModel extends GenericModel {
    
    @Id
    @GeneratedValue
    private Long id;
    @Required
    @MaxSize(100)
    @MinSize(3)
    private String name;
    @Required
    @MaxSize(500)
    @MinSize(3)
    private String startupScript;
    @Required
    @ManyToMany
    @JoinTable(name = "tb_application_image", 
        joinColumns = @JoinColumn(name = "id_application", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "id_image", referencedColumnName = "id"))
    private List<ImageModel> listImages;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getStartupScript() {
        return startupScript;
    }
    public void setStartupScript(String startupScript) {
        this.startupScript = startupScript;
    }
    public List<ImageModel> getListImages() {
        return listImages;
    }
    public void setListImages(List<ImageModel> listImages) {
        this.listImages = listImages;
    }
}
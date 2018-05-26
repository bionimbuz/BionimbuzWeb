package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_space_file")
public class SpaceFileModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private SpaceModel space;
    @Required
    @MaxSize(100)
    private String name;
    @Required
    @MaxSize(200)
    private String url;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public SpaceModel getSpace() {
        return space;
    }
    public void setSpace(SpaceModel space) {
        this.space = space;
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

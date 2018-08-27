package models;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Inheritance
@DiscriminatorColumn(name="type")
@Table(name = "tb_application_file")
public class ApplicationFileModel extends GenericModel {
    
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SpaceFileModel spaceFile;
    private Integer order;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public ApplicationFileModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public final Long getId() {
        return id;
    }
    public final SpaceFileModel getSpaceFile() {
        return spaceFile;
    }
    public final void setSpaceFile(SpaceFileModel spaceFile) {
        this.spaceFile = spaceFile;
    }
    public final Integer getOrder() {
        return order;
    }
    public final void setOrder(Integer order) {
        this.order = order;
    }
    public final void setId(Long id) {
        this.id = id;
    }
}

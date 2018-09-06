package models;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DiscriminatorOptions;

import play.db.jpa.GenericModel;

@Entity
@Inheritance
@DiscriminatorColumn(name = "file_type")
@DiscriminatorOptions(force = true)
@Table(name = "tb_application_file")
public class ApplicationFileModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.REMOVE
    })
    @JoinColumn(nullable = false)
    private SpaceFileModel spaceFile;
    private Integer fileOrder;

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
        return this.id;
    }

    public final SpaceFileModel getSpaceFile() {
        return this.spaceFile;
    }

    public final void setSpaceFile(final SpaceFileModel spaceFile) {
        this.spaceFile = spaceFile;
    }

    public final Integer getFileOrder() {
        return this.fileOrder;
    }

    public final void setFileOrder(final Integer order) {
        this.fileOrder = order;
    }

    public final void setId(final Long id) {
        this.id = id;
    }
}

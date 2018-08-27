package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_application_file")
public class ApplicationFileModel extends GenericModel {

    public static enum FILE_TYPE {
        INPUT,
        OUTPUT
    }
    
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SpaceFileModel spaceFile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ApplicationArgumentsModel applicationArguments;
    private Integer order;
    @Enumerated(EnumType.STRING)
    private FILE_TYPE fileType;

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
    public final FILE_TYPE getFileType() {
        return fileType;
    }
    public final void setFileType(FILE_TYPE fileType) {
        this.fileType = fileType;
    }
    public final void setId(Long id) {
        this.id = id;
    }
    public final ApplicationArgumentsModel getApplicationArguments() {
        return applicationArguments;
    }
    public final void setApplicationArguments(
            ApplicationArgumentsModel applicationArguments) {
        this.applicationArguments = applicationArguments;
    }    
}

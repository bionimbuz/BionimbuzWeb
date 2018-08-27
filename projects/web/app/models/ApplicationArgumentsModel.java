package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_application_arguments")
public class ApplicationArgumentsModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, mappedBy="applicationArguments")
    private InstanceModel instance;
    private String arguments;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "applicationArguments")
    private List<ApplicationFileInputModel> applicationInputFiles;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "applicationArguments")
    private List<ApplicationFileOutputModel> applicationOutputFiles;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public ApplicationArgumentsModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public final Long getId() {
        return id;
    }
    public final void setId(Long id) {
        this.id = id;
    }
    public final InstanceModel getInstance() {
        return instance;
    }
    public final void setInstance(InstanceModel instance) {
        this.instance = instance;
    }
    public final String getArguments() {
        return arguments;
    }
    public final void setArguments(String arguments) {
        this.arguments = arguments;
    }
    public final List<ApplicationFileInputModel> getApplicationInputFiles() {
        return applicationInputFiles;
    }
    public final void setApplicationInputFiles(
            List<ApplicationFileInputModel> applicationInputFiles) {
        this.applicationInputFiles = applicationInputFiles;
    }
    public final List<ApplicationFileOutputModel> getApplicationOutputFiles() {
        return applicationOutputFiles;
    }
    public final void setApplicationOutputFiles(
            List<ApplicationFileOutputModel> applicationOuputFiles) {
        this.applicationOutputFiles = applicationOuputFiles;
    }    
}

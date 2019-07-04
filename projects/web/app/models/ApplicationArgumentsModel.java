package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "applicationArguments")
    private InstanceModel instance;
    private String arguments;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "applicationArguments", cascade = {
            CascadeType.PERSIST,
            CascadeType.REMOVE
    }, orphanRemoval = true)
    private List<ApplicationFileInputModel> applicationInputFiles;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "applicationArguments", cascade = {
            CascadeType.PERSIST,
            CascadeType.REMOVE
    }, orphanRemoval = true)
    private List<ApplicationFileOutputModel> applicationOutputFiles;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public ApplicationArgumentsModel() {
        super();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Transients
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean addInputFile(final ApplicationFileInputModel inputFile) {
        if (this.applicationInputFiles == null) {
            this.applicationInputFiles = new ArrayList<>();
        }
        inputFile.setApplicationArguments(this);
        return this.applicationInputFiles.add(inputFile);
    }

    public boolean addOutputFile(final ApplicationFileOutputModel outputFile) {
        if (this.applicationOutputFiles == null) {
            this.applicationOutputFiles = new ArrayList<>();
        }
        outputFile.setApplicationArguments(this);
        return this.applicationOutputFiles.add(outputFile);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public InstanceModel getInstance() {
        return this.instance;
    }

    public void setInstance(final InstanceModel instance) {
        this.instance = instance;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setArguments(final String arguments) {
        this.arguments = arguments;
    }

    public List<ApplicationFileInputModel> getApplicationInputFiles() {
        return this.applicationInputFiles;
    }

    public void setApplicationInputFiles(
            final List<ApplicationFileInputModel> applicationInputFiles) {
        this.applicationInputFiles = applicationInputFiles;
    }

    public List<ApplicationFileOutputModel> getApplicationOutputFiles() {
        return this.applicationOutputFiles;
    }

    public void setApplicationOutputFiles(
            final List<ApplicationFileOutputModel> applicationOuputFiles) {
        this.applicationOutputFiles = applicationOuputFiles;
    }
}

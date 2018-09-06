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

    public final Long getId() {
        return this.id;
    }

    public final void setId(final Long id) {
        this.id = id;
    }

    public final InstanceModel getInstance() {
        return this.instance;
    }

    public final void setInstance(final InstanceModel instance) {
        this.instance = instance;
    }

    public final String getArguments() {
        return this.arguments;
    }

    public final void setArguments(final String arguments) {
        this.arguments = arguments;
    }

    public final List<ApplicationFileInputModel> getApplicationInputFiles() {
        return this.applicationInputFiles;
    }

    public final void setApplicationInputFiles(
            final List<ApplicationFileInputModel> applicationInputFiles) {
        this.applicationInputFiles = applicationInputFiles;
    }

    public final List<ApplicationFileOutputModel> getApplicationOutputFiles() {
        return this.applicationOutputFiles;
    }

    public final void setApplicationOutputFiles(
            final List<ApplicationFileOutputModel> applicationOuputFiles) {
        this.applicationOutputFiles = applicationOuputFiles;
    }
}

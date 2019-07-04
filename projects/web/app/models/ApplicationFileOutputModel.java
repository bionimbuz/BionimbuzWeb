package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("O")
public class ApplicationFileOutputModel extends ApplicationFileModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ApplicationArgumentsModel applicationArguments;
    
    public ApplicationFileOutputModel() {        
    }
    
    public ApplicationArgumentsModel getApplicationArguments() {
        return applicationArguments;
    }
    public void setApplicationArguments(
            ApplicationArgumentsModel applicationArguments) {
        this.applicationArguments = applicationArguments;
    }
}

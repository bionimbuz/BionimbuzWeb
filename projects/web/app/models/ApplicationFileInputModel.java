package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("I")
public class ApplicationFileInputModel extends ApplicationFileModel {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ApplicationArgumentsModel applicationArguments;

    public ApplicationFileInputModel() {        
    }
    
    public final ApplicationArgumentsModel getApplicationArguments() {
        return applicationArguments;
    }
    public final void setApplicationArguments(
            ApplicationArgumentsModel applicationArguments) {
        this.applicationArguments = applicationArguments;
    }
}

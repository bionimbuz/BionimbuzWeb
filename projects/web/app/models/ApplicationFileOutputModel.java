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
    
    public static class ApplicationOutput {
        private Integer spaceId;
        private String name;
        
        public final Integer getSpaceId() {
            return spaceId;
        }
        public final void setSpaceId(Integer spaceId) {
            this.spaceId = spaceId;
        }
        public final String getName() {
            return name;
        }
        public final void setName(String name) {
            this.name = name;
        }
    }

    public ApplicationFileOutputModel() {        
    }
    
    public final ApplicationArgumentsModel getApplicationArguments() {
        return applicationArguments;
    }
    public final void setApplicationArguments(
            ApplicationArgumentsModel applicationArguments) {
        this.applicationArguments = applicationArguments;
    }
}

package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("C")
public class CoordinatorModel extends ApplicationModel {
    
    public static CoordinatorModel first() {
        CoordinatorModel object = find("").first();
        return object;
    }
}

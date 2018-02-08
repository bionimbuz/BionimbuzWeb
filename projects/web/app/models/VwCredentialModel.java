package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Subselect;

import play.db.jpa.GenericModel;

@Entity
@Subselect("select id from tb_credential")
public class VwCredentialModel extends GenericModel {
    
    @Id 
    String id;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}

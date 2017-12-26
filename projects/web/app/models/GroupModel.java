package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_group")
public class GroupModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @Required
    @MaxSize(100)
    @MinSize(3)
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private List<UserGroupModel> listUserGroups;
    @Transient
    private String strUsers;
    @Transient
    private List<Long> selectedUsers;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public List<UserGroupModel> getListUserGroups() {
        return listUserGroups;
    }
    public void setListUserGroups(List<UserGroupModel> listUserGroups) {
        this.listUserGroups = listUserGroups;
    }
    public String getStrUsers() {
        return strUsers;
    }
    public void setStrUsers(String strUsers) {
        this.strUsers = strUsers;
    }        
    public List<Long> getSelectedUsers() {
        return selectedUsers;
    }
    public void setSelectedUsers(List<Long> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

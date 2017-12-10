package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_profile")
public class RoleModel extends GenericModel {
     
    public static enum RoleType {
        ADMIN,
        NORMAL
    }
    
    @Id
    @Enumerated(EnumType.STRING)
    private RoleType id;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private List<UserModel> listUsers; 
    @Required
    @ManyToMany
    @JoinTable(name = "tb_profile_menu", 
        joinColumns = @JoinColumn(name = "id_role", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "id_menu", referencedColumnName = "id"))
    private List<MenuModel> listMenus;
    
    public RoleModel() {
        super();
    }

    public RoleType getId() {
        return id;
    }
    public void setId(RoleType id) {
        this.id = id;
    }
    public List<UserModel> getListUsers() {
        return listUsers;
    }
    public void setListUsers(List<UserModel> listUsers) {
        this.listUsers = listUsers;
    }
    public List<MenuModel> getListMenus() {
        return listMenus;
    }
    public void setListMenus(List<MenuModel> listMenus) {
        this.listMenus = listMenus;
    }
}

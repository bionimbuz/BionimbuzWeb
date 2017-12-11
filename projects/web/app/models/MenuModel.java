package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import models.RoleModel.RoleType;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_menu")
public class MenuModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private boolean enabled = true;
    private String path;    
    private String name;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "listMenus")
    private List<RoleModel> listRoles;
    
    public MenuModel() {  
        super();
    }
    
    public static boolean containsMenuProfile(final String path, final RoleType roleType) {
        return find(
                " SELECT menu "
                + " FROM MenuModel menu "
                + " JOIN menu.listRoles roles"
                + " WHERE ?1 = roles.id "
                + "     AND ?2 = menu.path", 
                    roleType,
                    path).first() != null;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<RoleModel> getListRoles() {
        return listRoles;
    }
    public void setListRoles(List<RoleModel> listRoles) {
        this.listRoles = listRoles;
    }
}

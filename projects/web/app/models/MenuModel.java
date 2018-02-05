package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import controllers.adm.BaseAdminController;
import models.RoleModel.RoleType;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_menu")
public class MenuModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    private String path;    
    private String name;
    private String iconClass;
    private Short menuOrder;
    @ManyToOne
    private MenuModel parentMenu;
    @OneToMany(mappedBy="parentMenu")
    @OrderBy("menuOrder ASC")
    private List<MenuModel> listChildrenMenus;
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
    
    public static List<MenuModel> searchMenus() {     
        UserModel user = BaseAdminController.getConnectedUser();
        return find(
                " SELECT DISTINCT menu "
                + " FROM MenuModel menu "
                + " LEFT JOIN FETCH menu.listChildrenMenus children"
                + " JOIN menu.listRoles roles"
                + " LEFT JOIN children.listRoles rolesChildren"
                + " WHERE menu.parentMenu IS NULL AND ?1 = roles.id AND"
                + "     ( rolesChildren IS NULL ) OR ( rolesChildren IS NOT NULL AND ?1 = rolesChildren.id )"
                + " ORDER BY menu.menuOrder", user.getRole().getId()).fetch();
    }

    public String getIconClass() {
        return iconClass;
    }
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
    public List<MenuModel> getListChildrenMenus() {
        return listChildrenMenus;
    }
    public void setListChildrenMenus(List<MenuModel> listChildrenMenus) {
        this.listChildrenMenus = listChildrenMenus;
    }
    public MenuModel getParentMenu() {
        return parentMenu;
    }
    public void setParentMenu(MenuModel parentMenu) {
        this.parentMenu = parentMenu;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Short getMenuOrder() {
        return menuOrder;
    }
    public void setMenuOrder(Short menuOrder) {
        this.menuOrder = menuOrder;
    }
}

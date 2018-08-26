package models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import common.utils.RandomString;
import controllers.security.SecurityController;
import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_user")
public class UserModel extends GenericModel {

    public static int DEFAULT_PASS_SIZE = 6;
    @Id
    @GeneratedValue
    private Long id;
    @Required
    @MaxSize(100)
    @MinSize(3)
    private String name;
    @Required
    @Unique
    @Email
    @MaxSize(100)
    @MinSize(5)
    private String email;
    @Required
    @Password
    @MaxSize(256)
    @MinSize(6)
    private String pass;
    private boolean joined = false;
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    private RoleModel role;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserGroupModel> listUserGroups;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<CredentialModel> listCredentials;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<SpaceModel> listSpaces;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "listUsers")
    private List<GroupModel> listGroups;
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static UserModel findByEmail(final String email) {
        return find("email = ?1", email).first();
    }
    public static UserModel createUser(final String email, final RoleModel role) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return createUser(email, new RandomString(DEFAULT_PASS_SIZE).nextString(), role);        
    }
    public static UserModel createUser(final String email, final String pass, final RoleModel role) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setRole(role);
        user.setPass(SecurityController.getSHA512(pass));
        user.setName(null);
        user.setJoined(false);
        user.save();
        
        return user;
    }

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
    public String getEmail() {
        return email;
    }
    public void setEmail(final String email) {
        this.email = email;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(final String pass) {
        this.pass = pass;
    }
    public RoleModel getRole() {
        return role;
    }
    public void setRole(RoleModel role) {
        this.role = role;
    }
    public List<UserGroupModel> getListUserGroups() {
        return listUserGroups;
    }
    public void setListUserGroups(List<UserGroupModel> listUserGroups) {
        this.listUserGroups = listUserGroups;
    }
    public boolean isJoined() {
        return joined;
    }
    public void setJoined(boolean joined) {
        this.joined = joined;
    }    
    public List<CredentialModel> getListCredentials() {
        return listCredentials;
    }
    public void setListCredentials(List<CredentialModel> listCredentials) {
        this.listCredentials = listCredentials;
    }    
    public List<GroupModel> getListGroups() {
        return listGroups;
    }
    public void setListGroups(List<GroupModel> listGroups) {
        this.listGroups = listGroups;
    }    
    public final List<SpaceModel> getListSpaces() {
        return listSpaces;
    }
    public final void setListSpaces(List<SpaceModel> listSpaces) {
        this.listSpaces = listSpaces;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

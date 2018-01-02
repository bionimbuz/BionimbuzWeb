package models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import common.utils.RandomString;
import models.RoleModel.RoleType;
import models.keys.UserGroupKey;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_user_group")
public class UserGroupModel extends GenericModel {

    @EmbeddedId 
    private UserGroupKey id;    
    @Required
    private boolean owner = false;
    @Required
    private boolean joined = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, insertable=false, updatable=false)
    private UserModel user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_group", nullable = false, insertable=false, updatable=false)
    private GroupModel group;  
    

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public UserGroupModel(UserModel user, GroupModel group) {
        this.user = user;
        this.group = group;        
        this.id = new UserGroupKey(user.getId(), group.getId());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Data access
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static void addUsersToGroup(
            final GroupModel group,
            String[] strUsers)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {        
        if(strUsers == null || strUsers.length == 0)
            return;        
        UserGroupModel userGroup;
        RoleModel role = RoleModel.findById(RoleType.NORMAL);
        RandomString randomStr = new RandomString(UserModel.DEFAULT_PASS_SIZE);
        
        for(String strUser : strUsers) {
            UserModel user = UserModel.findByEmail(strUser);
            if(user == null) {
                user = UserModel.createUser(strUser, randomStr.nextString(), role);
            }
            // Skip users already added to group 
            if(UserGroupModel.findById(
                    new UserGroupKey(user, group))!=null) {
                continue;
            }
            userGroup = new UserGroupModel(user, group);
            userGroup.setOwner(false);
            userGroup.setJoined(false);
            userGroup.save();            
        };
    }
    public static void deleteUsersFromGroup(final GroupModel group, final Set<Long> users) { 
        if(users == null)
            return;
        UserGroupModel.delete("group.id = ?1 AND user.id IN (?2)", 
                group.getId(), 
                users);
    }
    public static void updateGroupOwnersIgnoreCurrent(
            final GroupModel group, 
            final Set<Long> users, 
            final UserModel currentUser) {
        if(users == null)
            return;
        for(UserGroupModel userGroup : group.getListUserGroups()) {
            final Long userId = userGroup.getUser().getId();
            if(currentUser.getId() == userId)
                continue;
            if(users.contains(userId)){
                userGroup.setOwner(true);
            }
            else {
                userGroup.setOwner(false);
            }
            userGroup.save();                
        }
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public UserGroupKey getId() {
        return id;
    }
    public boolean isOwner() {
        return owner;
    }
    public void setOwner(boolean owner) {
        this.owner = owner;
    }
    public void setId(UserGroupKey id) {
        this.id = id;
    }
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public GroupModel getGroup() {
        return group;
    }
    public void setGroup(GroupModel group) {
        this.group = group;
    }
    public boolean isJoined() {
        return joined;
    }
    public void setJoined(boolean joined) {
        this.joined = joined;
    }
}

package models.keys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import models.GroupModel;
import models.UserModel;

@Embeddable
public class UserGroupKey implements Serializable {
    
    @Column(name = "id_group", nullable = false, updatable = false)
    private Long groupId;
    @Column(name = "id_user", nullable = false, updatable = false)
    private Long userId;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
    public UserGroupKey() {
    }
    public UserGroupKey(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }
    public UserGroupKey(UserModel user, GroupModel group) {
        this(user.getId(), group.getId());
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Getters and Setters
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    } 

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Overrides
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserGroupKey)) {
            return false;
        }
        final UserGroupKey castOther = (UserGroupKey) other;
        return this.groupId.equals(castOther.groupId) 
                && this.userId.equals(castOther.userId);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.groupId.hashCode();
        hash = hash * prime + this.userId.hashCode();
        return hash;
    }
}


package test.model;

import javax.persistence.Embeddable;

/**
 * An embedded bean that has package visibility (is not public).
 */
@Embeddable
public class UserRoleKey {

    private long userId;

    private int roleId;

    public UserRoleKey(final int roleId, final long userId) {
        this.roleId = roleId;
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(final int roleId) {
        this.roleId = roleId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }
}

package fdms.model;

import java.util.EnumSet;
import java.util.Set;

public class Admin extends User {

    public Admin(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public Set<Permission> getPermissions() {
        return EnumSet.of(
                Permission.VIEW_FLIGHTS,
                Permission.MANAGE_FLIGHTS,
                Permission.MANAGE_DISRUPTION,
                Permission.VIEW_ALL_TICKETS);
    }
}

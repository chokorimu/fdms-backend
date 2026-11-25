package fdms.model;

import java.util.EnumSet;
import java.util.Set;

public class Passenger extends User {

    public Passenger(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public Role getRole() {
        return Role.PENUMPANG;
    }

    @Override
    public Set<Permission> getPermissions() {
        return EnumSet.of(
                Permission.VIEW_FLIGHTS,
                Permission.MANAGE_OWN_TICKETS);
    }
}

package fdms.model;

import java.util.Set;

/**
 * Base class untuk semua pengguna sistem.
 * Setiap subclass menentukan role dan kemampuannya sendiri.
 */
public abstract class User {

    private final String username;
    private final String password;
    private final String name;

    protected User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public abstract Role getRole();

    public abstract Set<Permission> getPermissions();

    public boolean can(Permission permission) {
        return getPermissions().contains(permission);
    }
}

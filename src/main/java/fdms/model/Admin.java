package fdms.model;

public class Admin extends User {

    public Admin(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}

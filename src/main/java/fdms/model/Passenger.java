package fdms.model;

public class Passenger extends User {

    public Passenger(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public Role getRole() {
        return Role.PENUMPANG;
    }
}

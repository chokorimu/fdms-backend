package fdms.model;

public class Ticket {

    public final String id;
    public final User passenger;
    public Flight flight;
    public Handling handling = Handling.MENUNGGU_PILIHAN;
    public boolean escalated = false;

    public Ticket(String id, User passenger, Flight flight) {
        this.id = id;
        this.passenger = passenger;
        this.flight = flight;
    }
}

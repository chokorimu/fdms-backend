package fdms.model;

public class Flight {

    public final String code;
    public final String route;
    public final String departure;
    public final int capacity;
    public int seatsTaken;
    public FlightStatus status = FlightStatus.NORMAL;
    public int delayMinutes = 0;

    public Flight(String code, String route, String departure, int capacity) {
        this.code = code;
        this.route = route;
        this.departure = departure;
        this.capacity = capacity;
    }

    public int seatsLeft() {
        return capacity - seatsTaken;
    }

    public String describe() {
        String s = code + " | " + route + " | " + departure + " | status: " + status;
        if (status == FlightStatus.DELAYED) {
            s += " (" + delayMinutes + " menit)";
        }
        return s;
    }
}

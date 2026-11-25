package fdms.data;

import fdms.model.Admin;
import fdms.model.Flight;
import fdms.model.Passenger;
import fdms.model.Ticket;
import fdms.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Penyimpanan data in-memory. Ganti dengan repository (JPA) jika database sudah tersedia.
 */
public final class Database {

    private static final Map<String, User> USERS = new LinkedHashMap<>();
    private static final Map<String, Flight> FLIGHTS = new LinkedHashMap<>();
    private static final List<Ticket> TICKETS = new ArrayList<>();

    private Database() {
    }

    public static synchronized void seed() {
        USERS.clear();
        FLIGHTS.clear();
        TICKETS.clear();

        Admin admin = new Admin("admin", "admin123", "Admin FDMS");
        Passenger budi = new Passenger("budi", "budi123", "Budi");
        Passenger siti = new Passenger("siti", "siti123", "Siti");
        USERS.put(admin.getUsername(), admin);
        USERS.put(budi.getUsername(), budi);
        USERS.put(siti.getUsername(), siti);

        addFlight(new Flight("GA101", "CGK-DPS", "08:00", 180, 150));
        addFlight(new Flight("GA105", "CGK-DPS", "12:00", 180, 90));
        addFlight(new Flight("GA109", "CGK-DPS", "18:00", 150, 60));
        addFlight(new Flight("JT200", "CGK-SUB", "09:00", 150, 100));

        TICKETS.add(new Ticket("T001", budi, FLIGHTS.get("GA101")));
        TICKETS.add(new Ticket("T002", siti, FLIGHTS.get("GA101")));
    }

    public static synchronized Optional<User> findUser(String username) {
        return Optional.ofNullable(USERS.get(username));
    }

    public static synchronized Optional<Flight> findFlight(String code) {
        return Optional.ofNullable(FLIGHTS.get(code.toUpperCase()));
    }

    public static synchronized void addFlight(Flight flight) {
        FLIGHTS.put(flight.getCode(), flight);
    }

    public static synchronized boolean removeFlight(String code) {
        return FLIGHTS.remove(code.toUpperCase()) != null;
    }

    public static synchronized List<Flight> flights() {
        return List.copyOf(FLIGHTS.values());
    }

    public static synchronized List<Ticket> tickets() {
        return List.copyOf(TICKETS);
    }
}

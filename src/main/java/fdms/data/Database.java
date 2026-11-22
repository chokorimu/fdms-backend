package fdms.data;

import fdms.model.Flight;
import fdms.model.Role;
import fdms.model.Ticket;
import fdms.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Penyimpanan data in-memory. Ganti dengan database sungguhan jika diperlukan.
 */
public class Database {

    public static final Map<String, User> users = new LinkedHashMap<>();
    public static final Map<String, Flight> flights = new LinkedHashMap<>();
    public static final List<Ticket> tickets = new ArrayList<>();

    private Database() {
    }

    public static void seed() {
        users.put("admin", new User("admin", "admin123", "Admin FDMS", Role.ADMIN));
        users.put("budi", new User("budi", "budi123", "Budi", Role.PENUMPANG));
        users.put("siti", new User("siti", "siti123", "Siti", Role.PENUMPANG));

        addFlight(new Flight("GA101", "CGK-DPS", "08:00", 180), 150);
        addFlight(new Flight("GA105", "CGK-DPS", "12:00", 180), 90);
        addFlight(new Flight("GA109", "CGK-DPS", "18:00", 150), 60);
        addFlight(new Flight("JT200", "CGK-SUB", "09:00", 150), 100);

        tickets.add(new Ticket("T001", users.get("budi"), flights.get("GA101")));
        tickets.add(new Ticket("T002", users.get("siti"), flights.get("GA101")));
    }

    private static void addFlight(Flight flight, int seatsTaken) {
        flight.seatsTaken = seatsTaken;
        flights.put(flight.code, flight);
    }
}

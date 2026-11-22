package fdms.dto;

import fdms.model.Flight;
import fdms.model.FlightStatus;
import fdms.model.Handling;
import fdms.model.Role;
import fdms.model.Ticket;
import fdms.model.User;

import java.util.List;

/**
 * Objek request/response untuk API. Tidak menyertakan password.
 */
public final class Dto {

    private Dto() {
    }

    // ===== Request =====
    public record LoginRequest(String username, String password) {}

    public record StatusUpdateRequest(FlightStatus status, Integer delayMinutes) {}

    public record RescheduleRequest(String flightCode) {}

    public record CompensationRequest(boolean agree) {}

    // ===== Response =====
    public record LoginResponse(String token, String username, String name, Role role) {}

    public record UserDto(String username, String name, Role role) {
        public static UserDto from(User u) {
            return new UserDto(u.username, u.name, u.role);
        }
    }

    public record FlightDto(String code, String route, String departure, int capacity,
                            int seatsTaken, int seatsLeft, FlightStatus status, int delayMinutes) {
        public static FlightDto from(Flight f) {
            return new FlightDto(f.code, f.route, f.departure, f.capacity,
                    f.seatsTaken, f.seatsLeft(), f.status, f.delayMinutes);
        }
    }

    public record TicketDto(String id, String passengerUsername, String passengerName,
                            FlightDto flight, Handling handling, boolean escalated) {
        public static TicketDto from(Ticket t) {
            return new TicketDto(t.id, t.passenger.username, t.passenger.name,
                    FlightDto.from(t.flight), t.handling, t.escalated);
        }
    }

    public record TicketOptionsDto(TicketDto ticket, boolean disrupted, boolean compensationEligible,
                                   long compensationAmount, List<FlightDto> alternatives) {}

    public record DisruptionResultDto(FlightDto flight, int affectedPassengers) {}
}

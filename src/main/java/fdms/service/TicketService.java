package fdms.service;

import fdms.data.Database;
import fdms.dto.Dto.CompensationRequest;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.RescheduleRequest;
import fdms.dto.Dto.TicketDto;
import fdms.dto.Dto.TicketOptionsDto;
import fdms.exception.ApiException;
import fdms.model.Flight;
import fdms.model.Ticket;
import fdms.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    public static final long COMPENSATION_AMOUNT = 500_000L; // Rp (contoh)

    public List<TicketDto> myTickets(User user) {
        return Database.tickets().stream()
                .filter(t -> t.getPassenger().getUsername().equals(user.getUsername()))
                .map(TicketDto::from)
                .toList();
    }

    public List<TicketDto> allTickets() {
        return Database.tickets().stream().map(TicketDto::from).toList();
    }

    public TicketOptionsDto getOptions(User user, String ticketId) {
        Ticket ticket = ownedTicket(user, ticketId);
        Flight flight = ticket.getFlight();

        List<FlightDto> alternatives = flight.isDisrupted()
                ? findAlternatives(flight).stream().map(FlightDto::from).toList()
                : List.of();

        return new TicketOptionsDto(
                TicketDto.from(ticket),
                flight.isDisrupted(),
                flight.isCompensationEligible(),
                COMPENSATION_AMOUNT,
                alternatives);
    }

    public synchronized TicketDto reschedule(User user, String ticketId, RescheduleRequest req) {
        Ticket ticket = ownedTicket(user, ticketId);
        if (req == null || req.flightCode() == null) {
            throw ApiException.badRequest("flightCode wajib diisi");
        }
        Flight target = Database.findFlight(req.flightCode())
                .orElseThrow(() -> ApiException.notFound("Penerbangan tujuan tidak ditemukan"));

        ticket.reschedule(target);
        return TicketDto.from(ticket);
    }

    public synchronized TicketDto escalate(User user, String ticketId) {
        Ticket ticket = ownedTicket(user, ticketId);
        ticket.escalate();
        return TicketDto.from(ticket);
    }

    public synchronized TicketDto compensation(User user, String ticketId, CompensationRequest req) {
        Ticket ticket = ownedTicket(user, ticketId);
        if (req != null && req.agree()) {
            ticket.chooseCompensation();
        }
        return TicketDto.from(ticket);
    }

    public synchronized TicketDto noChoice(User user, String ticketId) {
        Ticket ticket = ownedTicket(user, ticketId);
        ticket.chooseNoAction();
        return TicketDto.from(ticket);
    }

    // ===== Helper =====

    private Ticket ownedTicket(User user, String ticketId) {
        return Database.tickets().stream()
                .filter(t -> t.getId().equals(ticketId)
                        && t.getPassenger().getUsername().equals(user.getUsername()))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("Tiket tidak ditemukan"));
    }

    private static List<Flight> findAlternatives(Flight original) {
        return Database.flights().stream()
                .filter(f -> f != original
                        && f.getRoute().equals(original.getRoute())
                        && !f.isDisrupted())
                .toList();
    }
}

package fdms.service;

import fdms.data.Database;
import fdms.dto.Dto.CompensationRequest;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.RescheduleRequest;
import fdms.dto.Dto.TicketDto;
import fdms.dto.Dto.TicketOptionsDto;
import fdms.exception.ApiException;
import fdms.model.Flight;
import fdms.model.FlightStatus;
import fdms.model.Handling;
import fdms.model.Ticket;
import fdms.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    public static final int MIN_DELAY_FOR_COMPENSATION = 120; // menit
    public static final long COMPENSATION_AMOUNT = 500_000L;  // Rp (contoh)

    public List<TicketDto> myTickets(User user) {
        return Database.tickets.stream()
                .filter(t -> t.passenger.username.equals(user.username))
                .map(TicketDto::from)
                .toList();
    }

    public List<TicketDto> allTickets() {
        return Database.tickets.stream().map(TicketDto::from).toList();
    }

    public TicketOptionsDto getOptions(User user, String ticketId) {
        Ticket ticket = ownedTicket(user, ticketId);
        boolean disrupted = ticket.flight.status != FlightStatus.NORMAL;
        List<FlightDto> alternatives = disrupted
                ? findAlternatives(ticket.flight).stream().map(FlightDto::from).toList()
                : List.of();

        return new TicketOptionsDto(
                TicketDto.from(ticket),
                disrupted,
                isCompensationEligible(ticket.flight),
                COMPENSATION_AMOUNT,
                alternatives);
    }

    public synchronized TicketDto reschedule(User user, String ticketId, RescheduleRequest req) {
        Ticket ticket = activeTicket(user, ticketId);
        if (req == null || req.flightCode() == null) {
            throw ApiException.badRequest("flightCode wajib diisi");
        }

        Flight target = Database.flights.get(req.flightCode().toUpperCase());
        if (target == null) {
            throw ApiException.notFound("Penerbangan tujuan tidak ditemukan");
        }
        if (target == ticket.flight
                || target.status != FlightStatus.NORMAL
                || !target.route.equals(ticket.flight.route)) {
            throw ApiException.badRequest("Penerbangan tujuan tidak valid");
        }
        if (target.seatsLeft() <= 0) {
            throw ApiException.conflict("Kursi tidak tersedia");
        }

        ticket.flight.seatsTaken--;   // lepas kursi lama
        target.seatsTaken++;          // pesan kursi baru
        ticket.flight = target;
        ticket.handling = Handling.RESCHEDULED;
        return TicketDto.from(ticket);
    }

    public synchronized TicketDto escalate(User user, String ticketId) {
        Ticket ticket = activeTicket(user, ticketId);
        ticket.escalated = true;
        return TicketDto.from(ticket);
    }

    public synchronized TicketDto compensation(User user, String ticketId, CompensationRequest req) {
        Ticket ticket = activeTicket(user, ticketId);
        if (!isCompensationEligible(ticket.flight)) {
            throw ApiException.badRequest("Tidak memenuhi syarat kompensasi");
        }
        if (req != null && req.agree()) {
            ticket.handling = Handling.KOMPENSASI;
        }
        return TicketDto.from(ticket);
    }

    public synchronized TicketDto noChoice(User user, String ticketId) {
        Ticket ticket = activeTicket(user, ticketId);
        ticket.handling = Handling.TIDAK_MEMILIH;
        return TicketDto.from(ticket);
    }

    // ===== helper =====

    private Ticket ownedTicket(User user, String ticketId) {
        return Database.tickets.stream()
                .filter(t -> t.id.equals(ticketId) && t.passenger.username.equals(user.username))
                .findFirst()
                .orElseThrow(() -> ApiException.notFound("Tiket tidak ditemukan"));
    }

    private Ticket activeTicket(User user, String ticketId) {
        Ticket ticket = ownedTicket(user, ticketId);
        if (ticket.handling != Handling.MENUNGGU_PILIHAN) {
            throw ApiException.conflict("Tiket sudah diproses: " + ticket.handling);
        }
        if (ticket.flight.status == FlightStatus.NORMAL) {
            throw ApiException.badRequest("Penerbangan tidak terdampak");
        }
        return ticket;
    }

    private static List<Flight> findAlternatives(Flight original) {
        return Database.flights.values().stream()
                .filter(f -> f != original
                        && f.route.equals(original.route)
                        && f.status == FlightStatus.NORMAL)
                .toList();
    }

    private static boolean isCompensationEligible(Flight f) {
        return f.status == FlightStatus.CANCELLED
                || (f.status == FlightStatus.DELAYED && f.delayMinutes >= MIN_DELAY_FOR_COMPENSATION);
    }
}

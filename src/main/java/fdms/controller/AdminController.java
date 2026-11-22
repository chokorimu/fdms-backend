package fdms.controller;

import fdms.dto.Dto.DisruptionResultDto;
import fdms.dto.Dto.StatusUpdateRequest;
import fdms.dto.Dto.TicketDto;
import fdms.model.Role;
import fdms.service.AuthService;
import fdms.service.FlightService;
import fdms.service.TicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService auth;
    private final FlightService flights;
    private final TicketService tickets;

    public AdminController(AuthService auth, FlightService flights, TicketService tickets) {
        this.auth = auth;
        this.flights = flights;
        this.tickets = tickets;
    }

    /** Input gangguan: status NORMAL / DELAYED / CANCELLED. */
    @PutMapping("/flights/{code}/status")
    public DisruptionResultDto updateStatus(@PathVariable String code,
                                            @RequestBody StatusUpdateRequest req,
                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requireRole(authorization, Role.ADMIN);
        return flights.updateStatus(code, req);
    }

    /** Pantau status penanganan semua tiket. */
    @GetMapping("/tickets")
    public List<TicketDto> allTickets(@RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requireRole(authorization, Role.ADMIN);
        return tickets.allTickets();
    }
}

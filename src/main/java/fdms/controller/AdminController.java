package fdms.controller;

import fdms.dto.Dto.CreateFlightRequest;
import fdms.dto.Dto.DisruptionResultDto;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.StatusUpdateRequest;
import fdms.dto.Dto.TicketDto;
import fdms.dto.Dto.UpdateFlightRequest;
import fdms.model.Permission;
import fdms.service.AuthService;
import fdms.service.FlightService;
import fdms.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/flights")
    public FlightDto createFlight(@RequestBody CreateFlightRequest req,
                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.MANAGE_FLIGHTS);
        return flights.createFlight(req);
    }

    @PutMapping("/flights/{code}")
    public FlightDto updateFlight(@PathVariable String code,
                                  @RequestBody UpdateFlightRequest req,
                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.MANAGE_FLIGHTS);
        return flights.updateFlight(code, req);
    }

    @DeleteMapping("/flights/{code}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String code,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.MANAGE_FLIGHTS);
        flights.deleteFlight(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/flights/{code}/status")
    public DisruptionResultDto updateStatus(@PathVariable String code,
                                            @RequestBody StatusUpdateRequest req,
                                            @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.MANAGE_DISRUPTION);
        return flights.updateStatus(code, req);
    }

    @GetMapping("/tickets")
    public List<TicketDto> allTickets(@RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.VIEW_ALL_TICKETS);
        return tickets.allTickets();
    }
}

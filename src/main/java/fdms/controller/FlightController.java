package fdms.controller;

import fdms.dto.Dto.FlightDto;
import fdms.model.Permission;
import fdms.service.AuthService;
import fdms.service.FlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final AuthService auth;
    private final FlightService flights;

    public FlightController(AuthService auth, FlightService flights) {
        this.auth = auth;
        this.flights = flights;
    }

    @GetMapping
    public List<FlightDto> list(@RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.VIEW_FLIGHTS);
        return flights.listFlights();
    }

    @GetMapping("/{code}")
    public FlightDto get(@PathVariable String code,
                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        auth.requirePermission(authorization, Permission.VIEW_FLIGHTS);
        return flights.getFlight(code);
    }
}

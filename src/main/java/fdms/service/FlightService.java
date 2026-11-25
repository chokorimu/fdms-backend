package fdms.service;

import fdms.data.Database;
import fdms.dto.Dto.CreateFlightRequest;
import fdms.dto.Dto.DisruptionResultDto;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.StatusUpdateRequest;
import fdms.dto.Dto.UpdateFlightRequest;
import fdms.exception.ApiException;
import fdms.model.Flight;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    public List<FlightDto> listFlights() {
        return Database.flights().stream().map(FlightDto::from).toList();
    }

    public FlightDto getFlight(String code) {
        return FlightDto.from(findFlight(code));
    }

    public synchronized FlightDto createFlight(CreateFlightRequest req) {
        if (req == null || clean(req.code()) == null) {
            throw ApiException.badRequest("code wajib diisi");
        }
        if (req.capacity() == null) {
            throw ApiException.badRequest("capacity wajib diisi");
        }
        String code = clean(req.code()).toUpperCase();
        if (Database.findFlight(code).isPresent()) {
            throw ApiException.conflict("Kode penerbangan " + code + " sudah ada");
        }

        Flight flight = new Flight(code, clean(req.route()), clean(req.departure()), req.capacity(), 0);
        Database.addFlight(flight);
        return FlightDto.from(flight);
    }

    public synchronized FlightDto updateFlight(String code, UpdateFlightRequest req) {
        if (req == null || req.capacity() == null) {
            throw ApiException.badRequest("capacity wajib diisi");
        }
        Flight flight = findFlight(code);
        flight.updateDetails(clean(req.route()), clean(req.departure()), req.capacity());
        return FlightDto.from(flight);
    }

    public synchronized void deleteFlight(String code) {
        Flight flight = findFlight(code);
        boolean hasTickets = Database.tickets().stream().anyMatch(t -> t.getFlight() == flight);
        if (hasTickets) {
            throw ApiException.conflict("Penerbangan masih memiliki tiket dan tidak dapat dihapus");
        }
        Database.removeFlight(flight.getCode());
    }

    public synchronized DisruptionResultDto updateStatus(String code, StatusUpdateRequest req) {
        if (req == null || req.status() == null) {
            throw ApiException.badRequest("status wajib diisi");
        }
        Flight flight = findFlight(code);

        switch (req.status()) {
            case NORMAL -> flight.markNormal();
            case DELAYED -> {
                if (req.delayMinutes() == null) {
                    throw ApiException.badRequest("delayMinutes wajib diisi untuk status DELAYED");
                }
                flight.markDelayed(req.delayMinutes());
            }
            case CANCELLED -> flight.markCancelled();
        }

        long affected = Database.tickets().stream()
                .filter(t -> t.getFlight() == flight)
                .count();
        return new DisruptionResultDto(FlightDto.from(flight), (int) affected);
    }

    private Flight findFlight(String code) {
        return Database.findFlight(code)
                .orElseThrow(() -> ApiException.notFound("Penerbangan " + code + " tidak ditemukan"));
    }

    private static String clean(String value) {
        return value == null ? null : value.trim();
    }
}

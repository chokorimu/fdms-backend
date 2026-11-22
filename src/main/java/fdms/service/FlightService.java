package fdms.service;

import fdms.data.Database;
import fdms.dto.Dto.DisruptionResultDto;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.StatusUpdateRequest;
import fdms.exception.ApiException;
import fdms.model.Flight;
import fdms.model.FlightStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    public List<FlightDto> listFlights() {
        return Database.flights.values().stream().map(FlightDto::from).toList();
    }

    public FlightDto getFlight(String code) {
        return FlightDto.from(findFlight(code));
    }

    public synchronized DisruptionResultDto updateStatus(String code, StatusUpdateRequest req) {
        if (req == null || req.status() == null) {
            throw ApiException.badRequest("status wajib diisi");
        }
        Flight flight = findFlight(code);

        switch (req.status()) {
            case NORMAL -> {
                flight.status = FlightStatus.NORMAL;
                flight.delayMinutes = 0;
            }
            case DELAYED -> {
                if (req.delayMinutes() == null || req.delayMinutes() <= 0) {
                    throw ApiException.badRequest("delayMinutes wajib diisi dan lebih dari 0");
                }
                flight.status = FlightStatus.DELAYED;
                flight.delayMinutes = req.delayMinutes();
            }
            case CANCELLED -> {
                flight.status = FlightStatus.CANCELLED;
                flight.delayMinutes = 0;
            }
        }

        int affected = (int) Database.tickets.stream().filter(t -> t.flight == flight).count();
        return new DisruptionResultDto(FlightDto.from(flight), affected);
    }

    private Flight findFlight(String code) {
        Flight flight = Database.flights.get(code.toUpperCase());
        if (flight == null) {
            throw ApiException.notFound("Penerbangan " + code + " tidak ditemukan");
        }
        return flight;
    }
}

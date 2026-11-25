package fdms.service;

import fdms.data.Database;
import fdms.dto.Dto.DisruptionResultDto;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.StatusUpdateRequest;
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
}

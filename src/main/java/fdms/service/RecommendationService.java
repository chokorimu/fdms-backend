package fdms.service;

import fdms.dto.Dto.AlternativeDto;
import fdms.dto.Dto.FlightDto;
import fdms.dto.Dto.RecommendationDto;
import fdms.model.Flight;
import fdms.model.FlightStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    public static final String RESCHEDULE = "RESCHEDULE";
    public static final String VOUCHER = "VOUCHER";
    public static final String NONE = "NONE";

    /** Alternatif dianggap sesuai jika berangkat dalam rentang ini dari jam acuan. */
    static final int MAX_SHIFT_MINUTES = 180;

    /** Alternatif tertunda dianggap jelas lebih baik jika berangkat setidaknya selisih ini lebih awal. */
    static final int CLEARLY_EARLIER_MINUTES = 60;

    public List<AlternativeDto> rank(Flight original, List<Flight> candidates) {
        int reference = referenceMinutes(original);
        return candidates.stream()
                .map(c -> toAlternative(c, reference))
                .sorted(Comparator.comparing((AlternativeDto a) -> !a.withinWindow())
                        .thenComparingInt(a -> Math.abs(a.shiftMinutes()))
                        .thenComparingInt(a -> -a.flight().seatsLeft()))
                .toList();
    }

    public RecommendationDto recommend(Flight original, List<AlternativeDto> ranked) {
        boolean hasWithinWindow = ranked.stream().anyMatch(AlternativeDto::withinWindow);

        if (original.getStatus() == FlightStatus.CANCELLED) {
            return hasWithinWindow
                    ? new RecommendationDto(RESCHEDULE,
                        "Penerbangan dibatalkan dan tersedia alternatif dalam rentang " + MAX_SHIFT_MINUTES + " menit dari jam berangkat.")
                    : new RecommendationDto(VOUCHER,
                        "Penerbangan dibatalkan dan tidak ada alternatif dalam rentang " + MAX_SHIFT_MINUTES
                        + " menit. Voucher bernilai lebih besar daripada refund.");
        }

        if (original.getDelayMinutes() >= Flight.MIN_DELAY_FOR_COMPENSATION) {
            return hasWithinWindow
                    ? new RecommendationDto(RESCHEDULE,
                        "Penerbangan tertunda " + original.getDelayMinutes()
                        + " menit dan tersedia alternatif dalam rentang " + MAX_SHIFT_MINUTES + " menit.")
                    : new RecommendationDto(VOUCHER,
                        "Penerbangan tertunda " + original.getDelayMinutes()
                        + " menit dan tidak ada alternatif dalam rentang " + MAX_SHIFT_MINUTES
                        + " menit. Voucher bernilai lebih besar daripada refund.");
        }

        boolean clearlyEarlier = ranked.stream()
                .anyMatch(a -> a.withinWindow() && a.shiftMinutes() <= -CLEARLY_EARLIER_MINUTES);
        return clearlyEarlier
                ? new RecommendationDto(RESCHEDULE,
                    "Penerbangan tertunda kurang dari " + Flight.MIN_DELAY_FOR_COMPENSATION
                    + " menit, tetapi tersedia alternatif yang berangkat lebih awal.")
                : new RecommendationDto(NONE,
                    "Penerbangan tertunda kurang dari " + Flight.MIN_DELAY_FOR_COMPENSATION
                    + " menit dan tidak ada alternatif yang jelas lebih baik.");
    }

    private static AlternativeDto toAlternative(Flight candidate, int reference) {
        int shift = toMinutes(candidate.getDeparture()) - reference;
        boolean within = Math.abs(shift) <= MAX_SHIFT_MINUTES;
        String reason = describeShift(shift) + (within ? "" : ", di luar rentang " + MAX_SHIFT_MINUTES + " menit");
        return new AlternativeDto(FlightDto.from(candidate), shift, within, reason);
    }

    private static String describeShift(int shift) {
        if (shift == 0) return "berangkat pada jam acuan yang sama";
        if (shift < 0) return "berangkat " + (-shift) + " menit lebih awal dari jam acuan";
        return "berangkat " + shift + " menit lebih lambat dari jam acuan";
    }

    /** Jam acuan: jam berangkat asal, ditambah delay jika penerbangan tertunda. */
    private static int referenceMinutes(Flight f) {
        int base = toMinutes(f.getDeparture());
        return f.getStatus() == FlightStatus.DELAYED ? base + f.getDelayMinutes() : base;
    }

    private static int toMinutes(String hhmm) {
        String[] parts = hhmm.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}

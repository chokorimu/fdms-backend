package fdms.model;

public class Flight {

    public static final int MIN_DELAY_FOR_COMPENSATION = 120; // menit

    private final String code;
    private final String route;
    private final String departure;
    private final int capacity;
    private int seatsTaken;
    private FlightStatus status = FlightStatus.NORMAL;
    private int delayMinutes = 0;

    public Flight(String code, String route, String departure, int capacity, int seatsTaken) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Kapasitas harus lebih dari 0");
        }
        if (seatsTaken < 0 || seatsTaken > capacity) {
            throw new IllegalArgumentException("Jumlah kursi terisi tidak valid");
        }
        this.code = code;
        this.route = route;
        this.departure = departure;
        this.capacity = capacity;
        this.seatsTaken = seatsTaken;
    }

    public String getCode() {
        return code;
    }

    public String getRoute() {
        return route;
    }

    public String getDeparture() {
        return departure;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSeatsTaken() {
        return seatsTaken;
    }

    public int getSeatsLeft() {
        return capacity - seatsTaken;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public void reserveSeat() {
        if (getSeatsLeft() <= 0) {
            throw new IllegalStateException("Kursi tidak tersedia");
        }
        seatsTaken++;
    }

    public void releaseSeat() {
        if (seatsTaken <= 0) {
            throw new IllegalStateException("Tidak ada kursi yang bisa dilepas");
        }
        seatsTaken--;
    }

    public void markNormal() {
        status = FlightStatus.NORMAL;
        delayMinutes = 0;
    }

    public void markDelayed(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Durasi delay harus lebih dari 0 menit");
        }
        status = FlightStatus.DELAYED;
        delayMinutes = minutes;
    }

    public void markCancelled() {
        status = FlightStatus.CANCELLED;
        delayMinutes = 0;
    }

    public boolean isDisrupted() {
        return status != FlightStatus.NORMAL;
    }

    public boolean isCompensationEligible() {
        return status == FlightStatus.CANCELLED
                || (status == FlightStatus.DELAYED && delayMinutes >= MIN_DELAY_FOR_COMPENSATION);
    }
}

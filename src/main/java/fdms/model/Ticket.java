package fdms.model;

public class Ticket {

    private final String id;
    private final Passenger passenger;
    private Flight flight;
    private Handling handling = Handling.MENUNGGU_PILIHAN;
    private CompensationType compensationType;
    private boolean escalated = false;

    public Ticket(String id, Passenger passenger, Flight flight) {
        this.id = id;
        this.passenger = passenger;
        this.flight = flight;
    }

    public String getId() {
        return id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public Handling getHandling() {
        return handling;
    }

    public CompensationType getCompensationType() {
        return compensationType;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void reschedule(Flight target) {
        ensureOpen();
        if (target == flight) {
            throw new IllegalArgumentException("Penerbangan tujuan sama dengan penerbangan saat ini");
        }
        if (target.isDisrupted()) {
            throw new IllegalArgumentException("Penerbangan tujuan sedang terdampak");
        }
        if (!target.getRoute().equals(flight.getRoute())) {
            throw new IllegalArgumentException("Rute penerbangan tujuan berbeda");
        }

        target.reserveSeat();   // gagal lebih dulu jika kursi penuh, kursi lama belum dilepas
        flight.releaseSeat();
        flight = target;
        handling = Handling.RESCHEDULED;
    }

    public void chooseCompensation(CompensationType type) {
        ensureOpen();
        if (type == null) {
            throw new IllegalArgumentException("Bentuk kompensasi wajib dipilih");
        }
        if (!flight.isCompensationEligible()) {
            throw new IllegalArgumentException("Tidak memenuhi syarat kompensasi");
        }
        compensationType = type;
        handling = Handling.KOMPENSASI;
    }

    public void escalate() {
        ensureOpen();
        escalated = true;
    }

    public void chooseNoAction() {
        ensureOpen();
        handling = Handling.TIDAK_MEMILIH;
    }

    private void ensureOpen() {
        if (handling != Handling.MENUNGGU_PILIHAN) {
            throw new IllegalStateException("Tiket sudah diproses: " + handling);
        }
        if (!flight.isDisrupted()) {
            throw new IllegalStateException("Penerbangan tidak terdampak");
        }
    }
}

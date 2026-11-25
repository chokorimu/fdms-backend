package fdms.model;

public enum CompensationType {
    REFUND("Refund tunai", 100),
    VOUCHER("Voucher perjalanan", 150);

    private final String label;
    private final int percent;

    CompensationType(String label, int percent) {
        this.label = label;
        this.percent = percent;
    }

    public String getLabel() {
        return label;
    }

    public long amountFor(long baseAmount) {
        return baseAmount * percent / 100;
    }
}

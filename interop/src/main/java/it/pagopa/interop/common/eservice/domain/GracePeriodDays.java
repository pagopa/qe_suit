package it.pagopa.interop.common.eservice.domain;

public enum GracePeriodDays {
    NUMBER_30(30),

    NUMBER_60(60),

    NUMBER_90(90),

    NUMBER_120(120);

    private final int days;

    GracePeriodDays(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}

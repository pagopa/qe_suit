package it.pagopa.send.common.informal_notification.domain;

public record InformalPagoPaPaymentSpec(long amount, String dueDate) {

    public static final long DEFAULT_AMOUNT = 3000L;

    public static InformalPagoPaPaymentSpec of(long amount) {
        return new InformalPagoPaPaymentSpec(amount, null);
    }

    public static InformalPagoPaPaymentSpec of(long amount, String dueDate) {
        return new InformalPagoPaPaymentSpec(amount, dueDate);
    }

    public static InformalPagoPaPaymentSpec withDefaultAmount() {
        return new InformalPagoPaPaymentSpec(DEFAULT_AMOUNT, null);
    }
}

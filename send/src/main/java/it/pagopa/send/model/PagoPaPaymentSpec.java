package it.pagopa.send.model;

public record PagoPaPaymentSpec(long amount) implements PaymentSpec {

    private static final long DEFAULT_AMOUNT = 100L;

    public static PagoPaPaymentSpec withDefaultAmount() {
        return new PagoPaPaymentSpec(DEFAULT_AMOUNT);
    }
}

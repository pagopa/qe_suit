package it.pagopa.send.model;

public enum LegalNotificationType {
    SIMPLE,
    SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT,
    SINGLE_RECIPIENT_WITH_F24_PAYMENT;

    public boolean requiresPagoPaPayment() {
        return this == SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT;
    }
    public boolean requiresF24Payment() {
        return this == SINGLE_RECIPIENT_WITH_F24_PAYMENT;
    }
}

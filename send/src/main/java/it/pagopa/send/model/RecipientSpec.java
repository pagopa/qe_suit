package it.pagopa.send.model;

import it.pagopa.send.common.kernel.domain.Recipient;

import java.util.List;

/**
 * Un destinatario da inserire in una {@code BffNewNotificationRequest}, insieme agli avvisi di
 * pagamento (pagoPA e/o F24, zero o più) che gli vanno associati. Una notifica può avere più
 * destinatari: {@link it.pagopa.send.legalnotification.application.LegalNotificationJourney}
 * accumula uno o più {@code RecipientSpec}, la factory li traduce ciascuno nel proprio
 * {@code NotificationRecipientV24}.
 */
public record RecipientSpec(Recipient recipient, List<PaymentSpec> payments) {

    public static RecipientSpec of(Recipient recipient) {
        return new RecipientSpec(recipient, List.of());
    }

    public static RecipientSpec of(Recipient recipient, List<PaymentSpec> payments) {
        return new RecipientSpec(recipient, payments);
    }
}

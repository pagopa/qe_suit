package it.pagopa.send.model;

import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.journey.infrastructure.LegalNotificationJourneyImpl;

import java.util.List;

/**
 * Un destinatario censito ({@link Recipient}) da inserire in una notifica, con la possibilità di
 * sovrascrivere puntualmente {@code taxId}/{@code denomination} (es. per testare un codice fiscale
 * diverso da quello anagrafico del destinatario di test), il proprio indirizzo fisico/digitale e
 * gli avvisi di pagamento (pagoPA e/o F24, zero o più) associati. Una notifica può avere più
 * destinatari: {@link LegalNotificationJourneyImpl} accumula uno o più {@code RecipientSpec}, il
 * gateway del canale scelto li traduce ciascuno in un destinatario risolto.
 */
public record RecipientSpec(Recipient recipient, String taxIdOverride, String denominationOverride,
                             NotificationDefaults.PhysicalAddressDefaults physicalAddress,
                             String digitalDomicile, List<PaymentSpec> payments) {
}

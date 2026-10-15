package it.pagopa.send.common.legal_notification.domain;

/**
 * Default statici (nessun side effect, nessuna chiamata API) per una notifica, caricati
 * dall'unico template YAML in {@code notifications/templates/default.yaml}. Tutto ciò che invece
 * richiede una chiamata API o è calcolato a runtime (posizioni debitorie, preload documenti,
 * paProtocolNumber, group) resta gestito in
 * {@link it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory}.
 */
public record NotificationDefaults(
        String subject,
        String abstractText,
        String taxonomyCode,
        String notificationFeePolicy,
        String physicalCommunicationType,
        int paFee,
        int vat,
        int physicalCommunicationPriority,
        String pagoPaIntMode,
        PhysicalAddressDefaults physicalAddress
) {

    public record PhysicalAddressDefaults(
            String at,
            String address,
            String addressDetails,
            String zip,
            String municipality,
            String municipalityDetails,
            String province,
            String foreignState
    ) {
    }
}

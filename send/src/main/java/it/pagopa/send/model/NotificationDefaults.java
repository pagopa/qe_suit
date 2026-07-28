package it.pagopa.send.model;

/**
 * Default statici (nessun side effect, nessuna chiamata API) per un {@link LegalNotificationType},
 * caricati da un template YAML in {@code notifications/templates/}. Aggiungere un nuovo tipo di
 * notifica con la sua base di default è quindi solo un nuovo file YAML, non una modifica alla
 * factory. Tutto ciò che invece richiede una chiamata API o è calcolato a runtime (posizioni
 * debitorie, preload documenti, paProtocolNumber, group) resta gestito in
 * {@link it.pagopa.send.utils.factory.LegalNotificationRequestFactory}.
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

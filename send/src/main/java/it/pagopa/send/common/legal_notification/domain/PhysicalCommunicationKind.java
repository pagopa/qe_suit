package it.pagopa.send.common.legal_notification.domain;

/**
 * Vocabolario sintetico degli step per la tipologia di comunicazione fisica della notifica ("di tipo AR", "di tipo
 * 890"), tradotto nel {@link PhysicalCommunicationType} da impostare sul command di creazione.
 */
public enum PhysicalCommunicationKind {
    AR,
    NOTICE_890;

    public PhysicalCommunicationType physicalCommunication() {
        return switch (this) {
            case AR -> PhysicalCommunicationType.AR_REGISTERED_LETTER;
            case NOTICE_890 -> PhysicalCommunicationType.REGISTERED_LETTER_890;
        };
    }
}

package it.pagopa.send.common.legal_notification.domain;

import it.pagopa.send.common.user.domain.User;

import java.util.List;
import java.util.Map;

/**
 * Tutto quello che serve per creare una notifica legale: il mittente, i dati della notifica e i destinatari già risolti.
 * Trasporta solo i dati: la creazione la esegue il Journey.
 */
public record LegalNotificationCreationCommand(User sender, Map<String, String> notificationData,
                                               List<RecipientSpec> recipients) {

    /** Notifica con i dati di default: solo mittente e destinatari. */
    public LegalNotificationCreationCommand(User sender, List<RecipientSpec> recipients) {
        this(sender, Map.of(), recipients);
    }

    /** Solo mittente e dati: i destinatari si aggiungono dopo, uno alla volta. */
    public LegalNotificationCreationCommand(User sender, Map<String, String> notificationData) {
        this(sender, notificationData, List.of());
    }
}

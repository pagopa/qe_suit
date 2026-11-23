package it.pagopa.send.common.legal_notification.domain;

import it.pagopa.domain.Identifiable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Esito della validazione di una richiesta di notifica: accettata, oppure rifiutata con i codici degli errori di
 * validazione. Una richiesta rifiutata non diventa una notifica (non ha IUN), quindi il suo esito si registra a parte.
 */
public record LegalNotificationRequestOutcome(String notificationRequestId, NotificationStatus status,
                                              List<String> errorCodes) implements Identifiable {

    @Override
    public UUID getId() {
        return UUID.nameUUIDFromBytes(notificationRequestId.getBytes(StandardCharsets.UTF_8));
    }
}

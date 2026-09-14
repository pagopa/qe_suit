package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Casi d'uso per la creazione/gestione di una notifica legale: prepararla con i dati base,
 * aggiungerle destinatari (entrambi ripetibili/richiamabili da step Cucumber separati), inviarla e
 * attendere lo stato, eliminarla, leggerla, cercarla. Puro pass-through verso
 * {@link LegalNotificationGateway}: non ha logica né stato propri, sono le implementazioni del
 * gateway (una per canale) a sapere come procedere.
 */
@Service
@RequiredArgsConstructor
public class LegalNotificationUseCase {

    private final LegalNotificationGateway legalNotificationGateway;

    public void prepareNotification(Map<String, String> data) {
        legalNotificationGateway.prepareNotification(data);
    }

    public void addRecipient(RecipientSpec recipient) {
        legalNotificationGateway.addRecipient(recipient);
    }

    public void sendNotification(Tenant sender, NotificationStatus targetStatus) {
        legalNotificationGateway.sendNotification(sender, targetStatus);
    }

    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        legalNotificationGateway.waitForNotificationStatus(targetStatus);
    }

    public void deleteNotification(String iun) {
        legalNotificationGateway.deleteNotification(iun);
    }

    public LegalNotificationDomain readNotification(String iun) {
        return legalNotificationGateway.readNotification(iun);
    }

    public LegalNotificationDomain searchNotification(Map<String, String> overrides) {
        return legalNotificationGateway.searchNotification(overrides);
    }

}

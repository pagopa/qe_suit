package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Casi d'uso per la creazione/gestione di una notifica legale: prepararla con i dati base,
 * aggiungerle destinatari (entrambi ripetibili/richiamabili da step Cucumber separati), inviarla e
 * attendere lo stato, eliminarla, leggerla, cercarla. Puro pass-through verso un gateway per
 * ciascuna operazione: non ha logica né stato propri, sono le implementazioni dei gateway (una per
 * canale) a sapere come procedere.
 */
@Service
@RequiredArgsConstructor
public class LegalNotificationUseCase {

    private final PrepareNotificationGateway prepareNotificationGateway;
    private final AddRecipientGateway addRecipientGateway;
    private final SendNotificationGateway sendNotificationGateway;
    private final WaitForNotificationStatusGateway waitForNotificationStatusGateway;
    private final DeleteNotificationGateway deleteNotificationGateway;
    private final ReadNotificationGateway readNotificationGateway;
    private final SearchNotificationGateway searchNotificationGateway;

    public void prepareNotification(LegalNotificationCreationCommand command) {
        prepareNotificationGateway.prepareNotification(command);
    }

    public void addRecipient(RecipientSpec recipient) {
        addRecipientGateway.addRecipient(recipient);
    }

    public void sendNotification(User sender, NotificationStatus targetStatus) {
        sendNotificationGateway.sendNotification(sender, targetStatus);
    }

    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        waitForNotificationStatusGateway.waitForNotificationStatus(targetStatus);
    }

    public void deleteNotification(String iun) {
        deleteNotificationGateway.deleteNotification(iun);
    }

    public LegalNotificationDomain readNotification(String iun) {
        return readNotificationGateway.readNotification(iun);
    }

    public List<LegalNotificationDomain> searchNotification(Map<String, String> filters) {
        return searchNotificationGateway.searchNotification(filters);
    }
}

package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationDocumentType;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Casi d'uso per la creazione/gestione di una notifica legale: prepararla con i dati base,
 * aggiungerle destinatari (entrambi ripetibili/richiamabili da step Cucumber separati), inviarla e
 * attendere lo stato, eliminarla, leggerla, leggerne la cronologia, cercarla, ritrovarla tra quelle della PA e scaricarne i documenti e le attestazioni. Puro pass-through verso un gateway per
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
    private final ReadNotificationTimelineGateway readNotificationTimelineGateway;
    private final SearchNotificationGateway searchNotificationGateway;
    private final FindNotificationGateway findNotificationGateway;
    private final DownloadNotificationDocumentGateway downloadNotificationDocumentGateway;
    private final DownloadLegalFactsGateway downloadLegalFactsGateway;

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

    public NotificationTimeline readNotificationTimeline(String iun) {
        return readNotificationTimelineGateway.readNotificationTimeline(iun);
    }

    public List<LegalNotificationDomain> searchNotification(Map<String, String> filters) {
        return searchNotificationGateway.searchNotification(filters);
    }

    public LegalNotificationDomain findNotification(String iun, Map<String, String> filters) {
        return findNotificationGateway.findNotification(iun, filters);
    }

    public String downloadDocument(String iun, NotificationDocumentType type) {
        return downloadNotificationDocumentGateway.downloadDocument(iun, type);
    }

    public List<String> downloadLegalFacts(String iun) {
        return downloadLegalFactsGateway.downloadLegalFacts(iun);
    }
}

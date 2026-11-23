package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.application.LegalNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Crea la notifica dal portale della PA compilando la procedura guidata "Invia una nuova notifica", con gli stessi
 * dati di dominio del canale BFF; la sessione della PA mittente si apre alla navigazione, con l'utente corrente.
 */
@Service
@RequiredArgsConstructor
public class WebLegalNotificationGateway implements LegalNotificationGateway {

    private final WebPresentationGateway browser;
    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;

    /** Compila "Informazioni preliminari" (default di dominio e campi del command) e passa a "Destinatari". */
    @Override
    public void prepareNotification(LegalNotificationCreationCommand command) {
        requestFactory.applyPreliminaryData(request, command);
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);
        page.navigateTo();
        page.assertLoaded();
        page.compileInformazioniPreliminari(request);
        page.continueToNextStep();
        command.getRecipients().forEach(this::addRecipient);
    }

    /** Compila il destinatario nel passo "Destinatari", in un nuovo blocco se il precedente è già compilato. */
    @Override
    public void addRecipient(RecipientSpec recipient) {
        browser.bind(CreateNotificationPage.class).compileDestinatario(recipient);
    }

    @Override
    public void sendNotification(User sender, NotificationStatus targetStatus) {
        throw new UnsupportedOperationException("sendNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        throw new UnsupportedOperationException("waitForNotificationStatus non è stato ancora implementato per il canale Web");
    }

    @Override
    public void deleteNotification(String iun) {
        throw new UnsupportedOperationException("deleteNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        throw new UnsupportedOperationException("readNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> overrides) {
        throw new UnsupportedOperationException("searchNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

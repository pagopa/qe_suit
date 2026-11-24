package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.PrepareNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Apre "Invia una nuova notifica" nel portale della PA, compila "Informazioni preliminari" (default di dominio e campi
 * del command) e i destinatari del command; la sessione della PA mittente si apre alla navigazione, con l'utente
 * corrente.
 */
@Service
@RequiredArgsConstructor
public class WebPrepareNotificationGateway implements PrepareNotificationGateway {

    private final WebPresentationGateway browser;
    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;
    private final WebAddRecipientGateway addRecipientGateway;

    @Override
    public void prepareNotification(LegalNotificationCreationCommand command) {
        requestFactory.applyPreliminaryData(request, command);
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);
        page.navigateTo();
        page.assertLoaded();
        page.compileInformazioniPreliminari(request);
        page.continueToNextStep();
        command.getRecipients().forEach(addRecipientGateway::addRecipient);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

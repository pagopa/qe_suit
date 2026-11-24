package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.AddRecipientGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Compila il destinatario nel passo "Destinatari", in un nuovo blocco se il precedente è già compilato, e lo risolve
 * nella request: i suoi pagamenti, con la posizione debitoria, si compilano all'invio.
 */
@Service
@RequiredArgsConstructor
public class WebAddRecipientGateway implements AddRecipientGateway {

    private final WebPresentationGateway browser;
    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;

    @Override
    public void addRecipient(RecipientSpec recipient) {
        browser.bind(CreateNotificationPage.class).compileDestinatario(recipient);
        request.addRecipient(requestFactory.resolveRecipient(recipient));
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

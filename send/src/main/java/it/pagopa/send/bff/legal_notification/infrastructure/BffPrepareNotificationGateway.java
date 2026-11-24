package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.PrepareNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Predispone la notifica nella request del BFF: dati base, documento caricato con il preload e destinatari. */
@Service
@RequiredArgsConstructor
public class BffPrepareNotificationGateway implements PrepareNotificationGateway {

    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;
    private final BffAddRecipientGateway addRecipientGateway;

    @Override
    public void prepareNotification(LegalNotificationCreationCommand command) {
        requestFactory.applyPreliminaryData(request, command);
        requestFactory.applyDocument(request);
        command.getRecipients().forEach(addRecipientGateway::addRecipient);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

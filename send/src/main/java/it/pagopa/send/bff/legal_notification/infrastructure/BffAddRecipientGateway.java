package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.AddRecipientGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Aggiunge alla request del BFF il destinatario risolto, con le posizioni debitorie dei suoi pagamenti. */
@Service
@RequiredArgsConstructor
public class BffAddRecipientGateway implements AddRecipientGateway {

    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;

    @Override
    public void addRecipient(RecipientSpec recipient) {
        request.addRecipient(requestFactory.resolveRecipient(recipient));
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.ReadNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Legge la notifica dal BFF e la registra come ultima notifica dello scenario. */
@Service
@RequiredArgsConstructor
public class BffReadNotificationGateway implements ReadNotificationGateway {

    private final LegalNotificationRestClient restClient;
    private final BffLegalNotificationMapper mapper;

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        return restClient.read(iun)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toDomain)
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

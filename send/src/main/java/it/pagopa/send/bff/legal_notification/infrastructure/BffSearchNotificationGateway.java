package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.SearchNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** Cerca tra le notifiche della PA tramite il BFF. */
@Service
@RequiredArgsConstructor
public class BffSearchNotificationGateway implements SearchNotificationGateway {

    private final LegalNotificationRestClient restClient;
    private final BffLegalNotificationMapper mapper;

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> filters) {
        return restClient.search(filters)
                .withoutPolling()
                .map(response -> mapper.toDomainList(response.getResultsPage()))
                .get();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

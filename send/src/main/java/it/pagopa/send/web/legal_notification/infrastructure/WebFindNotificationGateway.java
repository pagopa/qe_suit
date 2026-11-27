package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.FindNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Ripete la ricerca tra le notifiche della PA nel portale finché tra i risultati compare quella con lo IUN dato.
 */
@Service
@RequiredArgsConstructor
public class WebFindNotificationGateway implements FindNotificationGateway {

    private static final Duration FIND_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration FIND_INTERVAL = Duration.ofSeconds(5);

    private final WebSearchNotificationGateway searchNotificationGateway;

    @Override
    public LegalNotificationDomain findNotification(String iun, Map<String, String> filters) {
        return PollingUtils.pollUntil(
                        () -> withIun(searchNotificationGateway.searchNotification(filters), iun),
                        Optional::isPresent,
                        FIND_TIMEOUT,
                        FIND_INTERVAL,
                        "La notifica " + iun + " non compare tra le notifiche della PA cercate con " + filters)
                .orElseThrow();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    private static Optional<LegalNotificationDomain> withIun(List<LegalNotificationDomain> results, String iun) {
        return results.stream().filter(result -> iun.equals(result.getIun())).findFirst();
    }
}

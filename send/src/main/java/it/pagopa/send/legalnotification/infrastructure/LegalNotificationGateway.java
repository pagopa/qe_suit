package it.pagopa.send.legalnotification.infrastructure;

import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.infrastructure.template.ApiResponse;
import it.pagopa.send.infrastructure.template.PollingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Non è un "gateway" nel senso di interop (nessuna interfaccia, nessun routing per canale: qui
 * esiste solo il BFF). È l'unico punto che sa come chiamare {@link LegalNotificationRestClient} e
 * come attendere, tramite polling, che una notifica raggiunga lo stato desiderato.
 */
@Service
@RequiredArgsConstructor
public class LegalNotificationGateway {

    /**
     * Lo stato ACCEPTED arriva tipicamente circa 5 minuti dopo la sottomissione della notifica;
     * finché non è disponibile, la GET risponde con un errore. Il timeout di default lascia un
     * margine di sicurezza; per attendere altri stati (che possono richiedere tempi diversi) si
     * può usare l'overload con timeout/interval espliciti.
     */
    private static final Duration DEFAULT_STATUS_TIMEOUT = Duration.ofMinutes(6);
    private static final Duration DEFAULT_STATUS_INTERVAL = Duration.ofSeconds(10);

    private final LegalNotificationRestClient restClient;

    public BffNewNotificationResponse create(BffNewNotificationRequest request) {
        return restClient.create(request)
                .withoutPolling()
                .get();
    }

    public BffFullNotificationV1 waitForStatus(String iun, BffNotificationStatus targetStatus) {
        return waitForStatus(iun, targetStatus, DEFAULT_STATUS_TIMEOUT, DEFAULT_STATUS_INTERVAL);
    }

    public BffFullNotificationV1 waitForStatus(String iun, BffNotificationStatus targetStatus, Duration timeout, Duration interval) {
        return restClient.read(iun)
                .withPolling(matchesStatus(targetStatus), timeout, interval)
                .get();
    }

    private PollingStrategy matchesStatus(BffNotificationStatus targetStatus) {
        return (ApiResponse response) -> response.is2xxSuccessful()
                && targetStatus.equals(response.as(BffFullNotificationV1.class).getNotificationStatus());
    }
}

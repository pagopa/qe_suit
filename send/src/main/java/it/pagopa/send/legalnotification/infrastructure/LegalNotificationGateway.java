package it.pagopa.send.legalnotification.infrastructure;

import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.*;
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
    private final NotificationContext notificationContext;

    public BffNewNotificationResponse create(BffNewNotificationRequest request) {
        BffNewNotificationResponse response = restClient.create(request)
                .withoutPolling()
                .get();

        notificationContext.setBffNewNotificationResponse(response);

        return response;
    }

    public BffRequestStatus delete(String iun) {
        return restClient.delete(iun)
                .withPolling(matchesStatus(BffNotificationStatus.CANCELLED), DEFAULT_STATUS_TIMEOUT, DEFAULT_STATUS_INTERVAL)
                .get();
    }

    public BffFullNotificationV1 readNotification(String iun, BffNotificationStatus targetStatus) {
        return restClient.read(iun)
                .withPolling(matchesStatus(targetStatus), DEFAULT_STATUS_TIMEOUT, DEFAULT_STATUS_INTERVAL)
                .get();
    }

    private PollingStrategy matchesStatus(BffNotificationStatus targetStatus) {
        return (ApiResponse response) -> response.is2xxSuccessful()
                && targetStatus.equals(response.as(BffFullNotificationV1.class).getNotificationStatus());
    }
}

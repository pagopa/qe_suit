package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDaysSeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
public class BffEServiceDescriptorArchiveGateway {
    private final BffEServiceRestClient restClient;

    public BffEServiceDescriptorArchiveGateway(BffEServiceRestClient restClient) {
        this.restClient = restClient;
    }

    public void archive(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            GracePeriodDays gracePeriodDays
    ) {
        GracePeriodDaysSeed payload = Instancio.of(GracePeriodDaysSeed.class)
                .set(field(GracePeriodDaysSeed::getGracePeriodDays),
                        it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays
                                .fromValue(gracePeriodDays.getDays()))
                .create();
        restClient.scheduleArchiveEserviceDescriptor(serviceRef.id(), descriptorRef.id(), payload)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }
}

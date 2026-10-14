package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceGateway;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffEServiceTemplateInstanceGateway implements EServiceTemplateInstanceGateway {
    private final BffEServiceRestClient restClient;
    private final BffEServiceGateway eServiceGateway;

    @Override
    public EService createEServiceInstance(EServiceTemplate template) {
        var created = restClient.createEServiceInstanceFromTemplate(
                        template.getId(), new InstanceEServiceSeed()
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return eServiceGateway.getEService(EServiceRef.of(created.getId()));
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

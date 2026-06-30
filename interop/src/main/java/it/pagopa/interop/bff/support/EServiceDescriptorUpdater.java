package it.pagopa.interop.bff.support;

import it.pagopa.interop.bff.service.mapper.EServiceMapper;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EServiceDescriptorUpdater {

    private final EServiceMapper mapper;
    private final ScenarioContext context;

    public EService upsert(UUID eserviceId, ProducerEServiceDescriptor descriptor) {
        EService currentEService = context
                .getById(eserviceId, EService.class)
                .orElse(null);

        return mapper.toDomainWithUpsert(descriptor, currentEService);
    }
}
package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceMapper;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingSeed;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Optional;

import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor
public class BffAttributeGateway implements AttributeGateway {

    private final BffAttributeRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
    private final EntityStore entityStore;
    private final BffAttributeMapper mapper;

    @Override
    public Attribute getAttribute(AttributeRef attributeRef) {
        return restClient.getAttribute(attributeRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAttribute)
                .updateContext()
                .get();
    }

    @Override
    public Attribute createCertifiedAttribute(Attribute attribute) {
        AttributeSeed seed = new AttributeSeed();
        seed.name(attribute.getName());
        seed.description(attribute.getDescription());

        TestChain<CreatedResource> createAttributeChain = restClient.createCertifiedAttribute(seed);
        return createAttributeChain
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(resource -> getAttribute(new AttributeRef(resource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    @Override
    public void removeCertifiedAttributeThreshold(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, EServiceDescriptorState state, int groupIndex, int attributeIndex) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void certifiedAttributeThresholdShouldBeRemoved() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

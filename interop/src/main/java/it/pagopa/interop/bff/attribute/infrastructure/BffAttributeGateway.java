package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BffAttributeGateway implements AttributeGateway {

    private final BffAttributeRestClient restClient;
    private final BffEServiceDescriptorGateway descriptorGateway;
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
    public Optional<Attribute> findAttributeByOriginAndCode(String origin, String code) {
        var result = restClient.getAttributeByOriginAndCode(origin, code)
                .withPolling(response -> response.isSuccess()
                        || response instanceof ApiResponse apiResponse && apiResponse.getStatusCode() == 404);
        if (result.getRaw() instanceof ApiResponse response && response.getStatusCode() == 404) {
            log.info("Attribute {}/{} is not present in the registry", origin, code);
            return Optional.empty();
        }
        return Optional.of(result.map(mapper::toAttribute).get());
    }

    @Override
    public Attribute createAttribute(Attribute attribute) {
        AttributeSeed seed = new AttributeSeed();
        seed.name(attribute.getName());
        seed.description(attribute.getDescription());

        TestChain<CreatedResource> createAttributeChain = switch (attribute.getKind()) {
            case CERTIFIED -> restClient.createCertifiedAttribute(seed);
            case DECLARED -> restClient.createDeclaredAttribute(seed);
            case VERIFIED -> restClient.createVerifiedAttribute(seed);
            case CERTIFIED_DISCRETE -> restClient.createCertifiedDiscreteAttribute(seed);
        };
        return createAttributeChain
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(resource -> getAttribute(new AttributeRef(resource.getId())))
                .updateContext()
                .get();
    }

    @Override
    public void removeCertifiedAttributeThreshold(EServiceRef eServiceRef, EServiceDescriptorRef eServiceDescriptorRef, EServiceDescriptorState state, int groupIndex, int attributeIndex) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void certifiedAttributeThresholdShouldBeRemoved() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Attribute createCertifiedAttribute(Attribute attribute) {
        return createAttribute(attribute);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

}

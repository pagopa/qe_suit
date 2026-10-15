package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BffAttributeGateway implements AttributeGateway {

    private final BffAttributeRestClient restClient;
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
}

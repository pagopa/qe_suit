package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BffAttributeGateway implements AttributeGateway {

    private final BffAttributeRestClient restClient;
    private final BffAttributeMapper mapper;
    private final EntityStore entityStore;

    @Override
    public Attribute getAttribute(UUID attributeId) {
        return restClient.getAttribute(attributeId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toAttribute)
                .updateContext()
                .get();
    }

    @Override
    public Attribute createCertifiedAttribute(AttributeSeed seed) {
        TestChain<CreatedResource> createAttributeChain = restClient.createCertifiedAttribute(seed);

        return createAttributeChain
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ref -> getAttribute(ref.getId()))
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

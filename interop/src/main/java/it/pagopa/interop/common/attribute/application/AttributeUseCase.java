package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AttributeUseCase {
    private final AttributeGateway attributeGateway;

    public Attribute getAttribute(UUID attributeId) {
        return attributeGateway.getAttribute(attributeId);
    }

    public Attribute createCertifiedAttribute() {
        AttributeSeed seed = new AttributeSeed()
                .name("attribute-" + Instant.now().getEpochSecond() + "-CERTIFIED")
                .description("Description Test");
        return attributeGateway.createCertifiedAttribute(seed);
    }
}

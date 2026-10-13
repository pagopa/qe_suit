package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AttributeUseCase {
    private final AttributeGateway attributeGateway;

    public Attribute getAttribute(AttributeRef attributeRef) {
        return attributeGateway.getAttribute(attributeRef);
    }

    public Attribute createCertifiedAttribute() {
        Attribute attribute = Attribute.builder()
                .name("attribute-" + Instant.now().getEpochSecond() + "-CERTIFIED")
                .description("Test Description")
                .build();
        return attributeGateway.createCertifiedAttribute(attribute);
    }

    public void removeCertifiedAttributeThreshold(EService eService, int groupIndex, int attributeIndex) {
        EServiceDescriptor lastEServiceDescriptor = eService.getLastDescriptor();
        attributeGateway.removeCertifiedAttributeThreshold(eService.getRef(), lastEServiceDescriptor.getRef(), lastEServiceDescriptor.getState(), groupIndex, attributeIndex);
    }

    public void certifiedAttributeThresholdRemoved() {
        attributeGateway.certifiedAttributeThresholdShouldBeRemoved();
    }
}

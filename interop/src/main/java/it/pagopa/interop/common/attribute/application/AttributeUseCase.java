package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AttributeUseCase {
    private final AttributeGateway attributeGateway;

    public Attribute getAttribute(AttributeRef attributeRef) {
        return attributeGateway.getAttribute(attributeRef);
    }

    public Attribute createCertifiedAttribute() {
        return createCertifiedAttribute("attribute-" + UUID.randomUUID() + "-CERTIFIED");
    }

    public Attribute createCertifiedAttribute(String name) {
        return createAttribute(AttributeKind.CERTIFIED, name);
    }

    public Attribute createAttribute(AttributeKind kind, String name) {
        java.util.Objects.requireNonNull(kind, "Attribute kind is required");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attribute name is required");
        }
        Attribute attribute = Attribute.builder()
                .name(name)
                .description("Test Description")
                .kind(kind)
                .build();
        return attributeGateway.createAttribute(attribute);
    }
}

package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequiredAttributeUseCase {
    private final AttributeGateway attributeGateway;

    public Attributes resolve(List<RequiredAttributeCommand> requests) {
        if (requests == null || requests.isEmpty() || requests.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException("Required attribute rows are mandatory");
        }
        List<Attribute> certified = new ArrayList<>();
        List<Attribute> declared = new ArrayList<>();
        List<Attribute> verified = new ArrayList<>();
        Set<UUID> attributeIds = new HashSet<>();

        for (RequiredAttributeCommand request : requests) {
            Attribute attribute = attributeGateway.getAttribute(request.getAttributeRef());
            if (attribute.getKind() != request.getKind()) {
                throw new IllegalStateException("Attribute " + attribute.getId() + " has kind "
                        + attribute.getKind() + ", expected " + request.getKind());
            }
            if (!attributeIds.add(attribute.getId())) {
                throw new IllegalArgumentException("An explicit attribute can be required only once: "
                        + attribute.getId());
            }
            Attribute requirement = attribute.toBuilder().group(request.getGroup()).build();
            switch (request.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> certified.add(requirement);
                case DECLARED -> declared.add(requirement);
                case VERIFIED -> verified.add(requirement);
            }
        }
        return Attributes.builder().certified(certified).declared(declared).verified(verified).build();
    }
}

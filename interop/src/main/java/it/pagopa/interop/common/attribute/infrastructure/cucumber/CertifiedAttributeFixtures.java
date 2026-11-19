package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CertifiedAttributeFixtures {
    private final Map<AttributeRef, TenantRef> createdAttributes = new LinkedHashMap<>();
    private final List<Assignment> assignments = new ArrayList<>();

    public void registerCreated(Attribute attribute, TenantRef issuer) {
        if (attribute.getKind() != AttributeKind.CERTIFIED) {
            throw new IllegalArgumentException("Scenario fixture must be a certified attribute");
        }
        createdAttributes.put(attribute.getRef(), issuer);
    }

    public void registerAssignment(TenantRef issuer, TenantRef tenant, AttributeRef attribute) {
        requireCreated(issuer, attribute);
        Assignment assignment = new Assignment(issuer, tenant, attribute);
        if (!assignments.contains(assignment)) {
            assignments.add(assignment);
        }
    }

    public void requireCreated(TenantRef issuer, AttributeRef attribute) {
        if (!issuer.equals(createdAttributes.get(attribute))) {
            throw new IllegalArgumentException("Only scenario-created attributes can be assigned by this fixture");
        }
    }

    public List<Assignment> assignments() {
        return List.copyOf(assignments);
    }

    public record Assignment(TenantRef issuer, TenantRef tenant, AttributeRef attribute) {
    }
}

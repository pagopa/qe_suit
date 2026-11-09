package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.attribute.domain.AttributeCertifiedDiscreteConfig;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import lombok.Value;

import java.util.Objects;

@Value
public class RequiredAttributeCommand {
    AttributeKind kind;
    int group;
    AttributeRef attributeRef;
    AttributeCertifiedDiscreteConfig discreteConfig;

    public RequiredAttributeCommand(AttributeKind kind, int group, AttributeRef attributeRef) {
        this(kind, group, attributeRef, null);
    }

    public RequiredAttributeCommand(
            AttributeKind kind, int group, AttributeRef attributeRef, AttributeCertifiedDiscreteConfig discreteConfig
    ) {
        this.kind = Objects.requireNonNull(kind, "Attribute kind is required");
        if (group < 1) throw new IllegalArgumentException("Attribute group must be positive");
        this.group = group;
        this.attributeRef = Objects.requireNonNull(attributeRef, "Attribute reference is required");
        if (kind == AttributeKind.CERTIFIED_DISCRETE) {
            if (discreteConfig == null || discreteConfig.getThreshold() == null
                    || discreteConfig.getComparator() == null) {
                throw new IllegalArgumentException(
                        "Discrete attributes require a discrete config with threshold and comparator");
            }
        } else if (discreteConfig != null) {
            throw new IllegalArgumentException("Discrete config is allowed only for discrete attributes");
        }
        this.discreteConfig = discreteConfig;
    }
}

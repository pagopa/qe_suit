package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import lombok.Value;

import java.util.Objects;

@Value
public class RequiredAttributeCommand {
    AttributeKind kind;
    int group;
    AttributeRef attributeRef;

    public RequiredAttributeCommand(AttributeKind kind, int group, AttributeRef attributeRef) {
        this.kind = Objects.requireNonNull(kind, "Attribute kind is required");
        if (group < 1) throw new IllegalArgumentException("Attribute group must be positive");
        this.group = group;
        this.attributeRef = Objects.requireNonNull(attributeRef, "Attribute reference is required");
    }
}

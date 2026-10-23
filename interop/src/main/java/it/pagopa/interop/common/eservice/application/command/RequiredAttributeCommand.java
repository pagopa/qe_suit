package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.AttributeOwnershipSelection;
import lombok.Value;

@Value
public class RequiredAttributeCommand {
    AttributeKind kind;
    int group;
    AttributeOwnershipSelection selection;

    public RequiredAttributeCommand(AttributeKind kind, int group, AttributeOwnershipSelection selection) {
        this.kind = java.util.Objects.requireNonNull(kind, "Attribute kind is required");
        if (group < 1) throw new IllegalArgumentException("Attribute group must be positive");
        this.group = group;
        this.selection = java.util.Objects.requireNonNull(selection, "Attribute selection is required");
    }
}

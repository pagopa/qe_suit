package it.pagopa.interop.bff.attribute.application;

import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import lombok.Getter;

@Getter
public class BffAttributeCreationCommand implements AttributeCreationCommand {
    private final AttributeSeed bffPayload;

    public BffAttributeCreationCommand() {
        this.bffPayload = new AttributeSeed();
    }

    private BffAttributeCreationCommand(AttributeSeed bffPayload) {
        this.bffPayload = bffPayload;
    }

    public static BffAttributeCreationCommand from(AttributeSeed payload) {
        return new BffAttributeCreationCommand(payload);
    }

    @Override
    public AttributeCreationCommand name(String name) {
        bffPayload.setName(name);
        return this;
    }

    @Override
    public AttributeCreationCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }
}


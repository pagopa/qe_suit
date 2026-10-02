package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import lombok.Getter;

public class BffEServiceTemplateCreationCommand implements EServiceTemplateCreationCommand {
    @Getter
    private final EServiceTemplateSeed bffCreationPayload;

    private BffEServiceTemplateCreationCommand(EServiceTemplateSeed bffCreationPayload) {
        this.bffCreationPayload = bffCreationPayload;
    }

    public static BffEServiceTemplateCreationCommand from(EServiceTemplateSeed creationSeed) {
        return new BffEServiceTemplateCreationCommand(creationSeed);
    }

    @Override
    public EServiceTemplateCreationCommand name(String name) {
        bffCreationPayload.setName(name);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand description(String description) {
        bffCreationPayload.setDescription(description);
        return this;
    }
}


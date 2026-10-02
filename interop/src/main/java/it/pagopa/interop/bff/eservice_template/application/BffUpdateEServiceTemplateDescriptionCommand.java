package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateDescriptionCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import lombok.Getter;

@Getter
public class BffUpdateEServiceTemplateDescriptionCommand implements UpdateEServiceTemplateDescriptionCommand {
    private final EServiceTemplateDescriptionUpdateSeed bffPayload;

    public static BffUpdateEServiceTemplateDescriptionCommand from(EServiceTemplateDescriptionUpdateSeed payload) {
        return new BffUpdateEServiceTemplateDescriptionCommand(payload);
    }

    private BffUpdateEServiceTemplateDescriptionCommand(EServiceTemplateDescriptionUpdateSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceTemplateDescriptionCommand() {
        this.bffPayload = new EServiceTemplateDescriptionUpdateSeed();
    }

    @Override
    public UpdateEServiceTemplateDescriptionCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }
}


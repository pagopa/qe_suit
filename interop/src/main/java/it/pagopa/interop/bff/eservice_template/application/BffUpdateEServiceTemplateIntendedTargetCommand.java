package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateIntendedTargetCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateIntendedTargetUpdateSeed;
import lombok.Getter;

@Getter
public class BffUpdateEServiceTemplateIntendedTargetCommand implements UpdateEServiceTemplateIntendedTargetCommand {
    private final EServiceTemplateIntendedTargetUpdateSeed bffPayload;

    public static BffUpdateEServiceTemplateIntendedTargetCommand from(EServiceTemplateIntendedTargetUpdateSeed payload) {
        return new BffUpdateEServiceTemplateIntendedTargetCommand(payload);
    }

    private BffUpdateEServiceTemplateIntendedTargetCommand(EServiceTemplateIntendedTargetUpdateSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceTemplateIntendedTargetCommand() {
        this.bffPayload = new EServiceTemplateIntendedTargetUpdateSeed();
    }

    @Override
    public UpdateEServiceTemplateIntendedTargetCommand intendedTarget(String intendedTarget) {
        bffPayload.setIntendedTarget(intendedTarget);
        return this;
    }
}


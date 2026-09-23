package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateNameCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import lombok.Getter;

@Getter
public class BffUpdateEServiceTemplateNameCommand implements UpdateEServiceTemplateNameCommand {
    private final EServiceTemplateNameUpdateSeed bffPayload;

    public static BffUpdateEServiceTemplateNameCommand from(EServiceTemplateNameUpdateSeed payload) {
        return new BffUpdateEServiceTemplateNameCommand(payload);
    }

    private BffUpdateEServiceTemplateNameCommand(EServiceTemplateNameUpdateSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceTemplateNameCommand() {
        this.bffPayload = new EServiceTemplateNameUpdateSeed();
    }

    @Override
    public UpdateEServiceTemplateNameCommand name(String name) {
        bffPayload.setName(name);
        return this;
    }
}


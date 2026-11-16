package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import lombok.Getter;

@Getter
public class BffEServiceTemplateInstanceCreationCommand implements EServiceTemplateInstanceCreationCommand {
    private final InstanceEServiceSeed bffPayload = new InstanceEServiceSeed();

    @Override
    public EServiceTemplateInstanceCreationCommand instanceLabel(String label) {
        bffPayload.setInstanceLabel(label);
        return this;
    }
}

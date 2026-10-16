package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import lombok.Getter;

@Getter
public class BffUpdateEServiceTemplateCommand implements UpdateEServiceTemplateCommand {
    private final UpdateEServiceTemplateSeed bffPayload;

    public static BffUpdateEServiceTemplateCommand from(UpdateEServiceTemplateSeed payload) {
        return new BffUpdateEServiceTemplateCommand(payload);
    }

    private BffUpdateEServiceTemplateCommand(UpdateEServiceTemplateSeed payload) {
        this.bffPayload = payload;
    }

    public BffUpdateEServiceTemplateCommand() {
        this.bffPayload = new UpdateEServiceTemplateSeed();
    }

    @Override
    public UpdateEServiceTemplateCommand name(String name) {
        bffPayload.setName(name);
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand intendedTarget(String intendedTarget) {
        bffPayload.setIntendedTarget(intendedTarget);
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand description(String description) {
        bffPayload.setDescription(description);
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand technology(EServiceTechnology technology) {
        bffPayload.setTechnology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.fromValue(technology.name()));
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand mode(EServiceMode mode) {
        bffPayload.setMode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.fromValue(mode.name()));
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        bffPayload.setIsSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand handlePersonalData(Boolean handlePersonalData) {
        bffPayload.setPersonalData(handlePersonalData);
        return this;
    }

    @Override
    public UpdateEServiceTemplateCommand isAsync(Boolean isAsync) {
        bffPayload.setAsyncExchange(isAsync);
        return this;
    }
}


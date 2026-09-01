package it.pagopa.interop.web.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import lombok.Getter;

public class WebEServiceCreationCommand implements EServiceCreationCommand {
    @Getter
    private final WebEServiceGeneralData webEServiceGeneralData;

    private WebEServiceCreationCommand(EServiceSeed webCreationPayload) {
        this.webEServiceGeneralData = new WebEServiceGeneralData(webCreationPayload);
    }

    public WebEServiceCreationCommand() {
        EServiceSeed seed = new EServiceSeed();
        this.webEServiceGeneralData = new WebEServiceGeneralData(seed);
    }

    public static WebEServiceCreationCommand from(EServiceSeed creationSeed) {
        return new WebEServiceCreationCommand(creationSeed);
    }

    @Override
    public EServiceCreationCommand name(String name) {
        webEServiceGeneralData.eservice().setName(name);
        return this;
    }

    @Override
    public EServiceCreationCommand mode(EServiceMode mode) {
        webEServiceGeneralData.eservice().setMode(
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.fromValue(mode.name())
        );
        return this;
    }

    @Override
    public EServiceCreationCommand isAsync(Boolean isAsync) {
        webEServiceGeneralData.eservice().asyncExchange(isAsync);
        return this;
    }

    @Override
    public EServiceCreationCommand technology(EServiceTechnology technology) {
        webEServiceGeneralData.eservice().technology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.fromValue(technology.name()));
        return this;
    }

    @Override
    public EServiceCreationCommand description(String description) {
        webEServiceGeneralData.eservice().setDescription(description);
        return this;
    }

    @Override
    public EServiceCreationCommand handlePersonalData(Boolean handlePersonalData) {
        webEServiceGeneralData.eservice().personalData(handlePersonalData);
        return this;
    }

    @Override
    public EServiceCreationCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        webEServiceGeneralData.eservice().isSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public EServiceCreationCommand isConsumerDelegable(Boolean isConsumerDelegable) {
        webEServiceGeneralData.eservice().isConsumerDelegable(isConsumerDelegable);
        return this;
    }

    @Override
    public EServiceCreationCommand isClientAccessDelegable(Boolean isClientAccessDelegable) {
        webEServiceGeneralData.eservice().isClientAccessDelegable(isClientAccessDelegable);
        return this;
    }
}

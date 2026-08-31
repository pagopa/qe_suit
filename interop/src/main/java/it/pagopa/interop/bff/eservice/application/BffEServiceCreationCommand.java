package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import lombok.Getter;

public class BffEServiceCreationCommand implements EServiceCreationCommand {
    @Getter
    private final EServiceSeed bffCreationPayload;

    private BffEServiceCreationCommand(EServiceSeed bffCreationPayload) {
        this.bffCreationPayload = bffCreationPayload;
    }

    public BffEServiceCreationCommand() {
        this.bffCreationPayload = new EServiceSeed();
    }

    public static BffEServiceCreationCommand from(EServiceSeed creationSeed) {
        return new BffEServiceCreationCommand(creationSeed);
    }

    @Override
    public EServiceCreationCommand name(String name) {
        bffCreationPayload.setName(name);
        return this;
    }

    @Override
    public EServiceCreationCommand mode(EServiceMode mode) {
        bffCreationPayload.setMode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.fromValue(mode.name()));
        return this;
    }

    @Override
    public EServiceCreationCommand isAsync(Boolean isAsync) {
        bffCreationPayload.asyncExchange(isAsync);
        return this;
    }

    @Override
    public EServiceCreationCommand technology(EServiceTechnology technology) {
        bffCreationPayload.technology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.fromValue(technology.name()));
        return this;
    }

    @Override
    public EServiceCreationCommand description(String description) {
        bffCreationPayload.setDescription(description);
        return this;
    }

    @Override
    public EServiceCreationCommand handlePersonalData(Boolean handlePersonalData) {
        bffCreationPayload.personalData(handlePersonalData);
        return this;
    }

    @Override
    public EServiceCreationCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        bffCreationPayload.isSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public EServiceCreationCommand isConsumerDelegable(Boolean isConsumerDelegable) {
        bffCreationPayload.isConsumerDelegable(isConsumerDelegable);
        return this;
    }

    @Override
    public EServiceCreationCommand isClientAccessDelegable(Boolean isClientAccessDelegable) {
        bffCreationPayload.isClientAccessDelegable(isClientAccessDelegable);
        return this;
    }
}

package it.pagopa.interop.bff.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.PatchUpdateEServiceSeed;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class BffEServiceUpdateCommand implements EServiceUpdateCommand {

    private final PatchUpdateEServiceSeed bffUpdatePayload;
    private String alias;

    public BffEServiceUpdateCommand() {
        this.bffUpdatePayload = new PatchUpdateEServiceSeed();
    }

    private BffEServiceUpdateCommand(PatchUpdateEServiceSeed bffUpdatePayload) {
        this.bffUpdatePayload = bffUpdatePayload;
    }

    public static BffEServiceUpdateCommand from(PatchUpdateEServiceSeed updateSeed) {
        return new BffEServiceUpdateCommand(updateSeed);
    }

    @Override
    public EServiceUpdateCommand alias(@Nullable String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public EServiceUpdateCommand asyncExchange(Boolean isAsync) {
        bffUpdatePayload.setAsyncExchange(isAsync);
        return this;
    }

    @Override
    public EServiceUpdateCommand name(String name) {
        bffUpdatePayload.setName(name);
        return this;
    }

    @Override
    public EServiceUpdateCommand description(String description) {
        bffUpdatePayload.setDescription(description);
        return this;
    }

    @Override
    public EServiceUpdateCommand technology(EServiceTechnology technology) {
        bffUpdatePayload.setTechnology(
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.fromValue(technology.name())
        );
        return this;
    }

    @Override
    public EServiceUpdateCommand mode(EServiceMode mode) {
        bffUpdatePayload.setMode(
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.fromValue(mode.name())
        );
        return this;
    }

    @Override
    public EServiceUpdateCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        bffUpdatePayload.setIsSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public EServiceUpdateCommand isConsumerDelegable(Boolean isConsumerDelegable) {
        bffUpdatePayload.setIsConsumerDelegable(isConsumerDelegable);
        return this;
    }

    @Override
    public EServiceUpdateCommand isClientAccessDelegable(Boolean isClientAccessDelegable) {
        bffUpdatePayload.setIsClientAccessDelegable(isClientAccessDelegable);
        return this;
    }

    @Override
    public EServiceUpdateCommand handlePersonalData(Boolean handlePersonalData) {
        bffUpdatePayload.personalData(handlePersonalData);
        return this;
    }
}

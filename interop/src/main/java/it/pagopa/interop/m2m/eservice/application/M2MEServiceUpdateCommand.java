package it.pagopa.interop.m2m.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceUpdateCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceDraftUpdateSeed;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class M2MEServiceUpdateCommand implements EServiceUpdateCommand {

    private final EServiceDraftUpdateSeed m2mUpdatePayload = new EServiceDraftUpdateSeed();
    private String alias;

    public M2MEServiceUpdateCommand alias(@Nullable String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand name(String name) {
        m2mUpdatePayload.setName(name);
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand description(String description) {
        m2mUpdatePayload.setDescription(description);
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand technology(EServiceTechnology technology) {
        m2mUpdatePayload.setTechnology(technology == null
                ? null
                : it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTechnology
                        .fromValue(technology.getValue()));
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand mode(EServiceMode mode) {
        m2mUpdatePayload.setMode(mode == null
                ? null
                : it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceMode
                        .fromValue(mode.getValue()));
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        m2mUpdatePayload.setIsSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand isConsumerDelegable(Boolean isConsumerDelegable) {
        m2mUpdatePayload.setIsConsumerDelegable(isConsumerDelegable);
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand isClientAccessDelegable(Boolean isClientAccessDelegable) {
        m2mUpdatePayload.setIsClientAccessDelegable(isClientAccessDelegable);
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand handlePersonalData(Boolean handlePersonalData) {
        m2mUpdatePayload.setPersonalData(handlePersonalData);
        return this;
    }

    @Override
    public M2MEServiceUpdateCommand asyncExchange(Boolean asyncExchange) {
        m2mUpdatePayload.setAsyncExchange(asyncExchange);
        return this;
    }
}

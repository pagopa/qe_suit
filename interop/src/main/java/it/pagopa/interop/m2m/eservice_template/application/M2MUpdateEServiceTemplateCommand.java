package it.pagopa.interop.m2m.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTemplateDraftUpdateSeed;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class M2MUpdateEServiceTemplateCommand implements UpdateEServiceTemplateCommand {
    private final EServiceTemplateDraftUpdateSeed m2mUpdatePayload = new EServiceTemplateDraftUpdateSeed();
    private String alias;

    @Override
    public M2MUpdateEServiceTemplateCommand name(String name) {
        m2mUpdatePayload.setName(name);
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand intendedTarget(String intendedTarget) {
        m2mUpdatePayload.setIntendedTarget(intendedTarget);
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand description(String description) {
        m2mUpdatePayload.setDescription(description);
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand technology(EServiceTechnology technology) {
        m2mUpdatePayload.setTechnology(technology == null
                ? null
                : it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceTechnology
                        .fromValue(technology.getValue()));
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand mode(EServiceMode mode) {
        m2mUpdatePayload.setMode(mode == null
                ? null
                : it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceMode
                        .fromValue(mode.getValue()));
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        m2mUpdatePayload.setIsSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand handlePersonalData(Boolean personalData) {
        m2mUpdatePayload.setPersonalData(personalData);
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand isAsync(Boolean asyncExchange) {
        // Per M2M non è previsto!
        return this;
    }

    @Override
    public M2MUpdateEServiceTemplateCommand alias(@Nullable String alias) {
        this.alias = alias;
        return this;
    }
}

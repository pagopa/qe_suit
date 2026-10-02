package it.pagopa.interop.bff.eservice_template.application;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import lombok.Getter;

public class BffEServiceTemplateCreationCommand implements EServiceTemplateCreationCommand {
    @Getter
    private final EServiceTemplateSeed bffCreationPayload;

    private BffEServiceTemplateCreationCommand(EServiceTemplateSeed bffCreationPayload) {
        this.bffCreationPayload = bffCreationPayload;
    }

    public BffEServiceTemplateCreationCommand() {
        this.bffCreationPayload = new EServiceTemplateSeed();
    }

    public static BffEServiceTemplateCreationCommand from(EServiceTemplateSeed seed) {
        return new BffEServiceTemplateCreationCommand(seed);
    }

    @Override
    public EServiceTemplateCreationCommand name(String name) {
        bffCreationPayload.setName(name);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand intendedTarget(String intendedTarget) {
        bffCreationPayload.setIntendedTarget(intendedTarget);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand description(String description) {
        bffCreationPayload.setDescription(description);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand technology(EServiceTechnology technology) {
        bffCreationPayload.setTechnology(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology.fromValue(technology.name()));
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand mode(EServiceMode mode) {
        bffCreationPayload.setMode(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.fromValue(mode.name()));
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand isSignalHubEnabled(Boolean isSignalHubEnabled) {
        bffCreationPayload.setIsSignalHubEnabled(isSignalHubEnabled);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand handlePersonalData(Boolean handlePersonalData) {
        bffCreationPayload.setPersonalData(handlePersonalData);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand isAsync(Boolean isAsync) {
        bffCreationPayload.setAsyncExchange(isAsync);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand versionDescription(String description) {
        getOrCreateVersionSeed().setDescription(description);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand voucherLifespan(Integer voucherLifespan) {
        getOrCreateVersionSeed().setVoucherLifespan(voucherLifespan);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand dailyCallsPerConsumer(Integer dailyCallsPerConsumer) {
        getOrCreateVersionSeed().setDailyCallsPerConsumer(dailyCallsPerConsumer);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand dailyCallsTotal(Integer dailyCallsTotal) {
        getOrCreateVersionSeed().setDailyCallsTotal(dailyCallsTotal);
        return this;
    }

    @Override
    public EServiceTemplateCreationCommand agreementApprovalPolicy(AgreementApprovalPolicy agreementApprovalPolicy) {
        getOrCreateVersionSeed().setAgreementApprovalPolicy(
                it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy.fromValue(
                        agreementApprovalPolicy.name()
                )
        );
        return this;
    }

    private VersionSeedForEServiceTemplateCreation getOrCreateVersionSeed() {
        if (bffCreationPayload.getVersion() == null) {
            bffCreationPayload.setVersion(new VersionSeedForEServiceTemplateCreation());
        }
        return bffCreationPayload.getVersion();
    }
}


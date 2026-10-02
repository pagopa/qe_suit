package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateCommand;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateDescriptionCommand;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateIntendedTargetCommand;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateNameCommand;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateDescriptionCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateIntendedTargetCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateNameCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateIntendedTargetUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import org.instancio.Instancio;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
public class BffEServiceTemplateRequestFactory implements EServiceTemplateRequestFactory {

    public EServiceTemplateSeed creationRequest() {
        return Instancio.of(EServiceTemplateSeed.class)
                .set(field(EServiceTemplateSeed::getName), randomAlphanumericName("eservice-template", 15))
                .set(field(EServiceTemplateSeed::getIntendedTarget), randomAlphanumericName("target", 30))
                .set(field(EServiceTemplateSeed::getDescription), randomAlphanumericName("description", 50))
                .set(field(EServiceTemplateSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceTemplateSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceTemplateSeed::getVersion), defaultVersionSeed())
                .set(field(EServiceTemplateSeed::getIsSignalHubEnabled), false)
                .set(field(EServiceTemplateSeed::getPersonalData), false)
                .set(field(EServiceTemplateSeed::getAsyncExchange), false)
                .create();
    }

    public EServiceTemplateNameUpdateSeed updateNameRequest() {
        return Instancio.of(EServiceTemplateNameUpdateSeed.class)
                .set(field(EServiceTemplateNameUpdateSeed::getName), randomAlphanumericName("eservice-template-name", 15))
                .create();
    }

    public EServiceTemplateIntendedTargetUpdateSeed updateIntendedTargetRequest() {
        return Instancio.of(EServiceTemplateIntendedTargetUpdateSeed.class)
                .set(field(EServiceTemplateIntendedTargetUpdateSeed::getIntendedTarget), randomAlphanumericName("target", 25))
                .create();
    }

    public EServiceTemplateDescriptionUpdateSeed updateDescriptionRequest() {
        return Instancio.of(EServiceTemplateDescriptionUpdateSeed.class)
                .set(field(EServiceTemplateDescriptionUpdateSeed::getDescription), randomAlphanumericName("description", 50))
                .create();
    }

    public UpdateEServiceTemplateSeed updateRequest() {
        EServiceTemplateSeed seed = creationRequest();
        return Instancio.of(UpdateEServiceTemplateSeed.class)
                .set(field(UpdateEServiceTemplateSeed::getName), seed.getName())
                .set(field(UpdateEServiceTemplateSeed::getIntendedTarget), seed.getIntendedTarget())
                .set(field(UpdateEServiceTemplateSeed::getDescription), seed.getDescription())
                .set(field(UpdateEServiceTemplateSeed::getTechnology), seed.getTechnology())
                .set(field(UpdateEServiceTemplateSeed::getMode), seed.getMode())
                .set(field(UpdateEServiceTemplateSeed::getIsSignalHubEnabled), seed.getIsSignalHubEnabled())
                .set(field(UpdateEServiceTemplateSeed::getPersonalData), seed.getPersonalData())
                .set(field(UpdateEServiceTemplateSeed::getAsyncExchange), seed.getAsyncExchange())
                .create();
    }

    public UpdateEServiceTemplateVersionSeed updateDraftVersionRequest() {
        return Instancio.of(UpdateEServiceTemplateVersionSeed.class)
                .set(field(UpdateEServiceTemplateVersionSeed::getDescription), randomAlphanumericName("version-description", 35))
                .set(field(UpdateEServiceTemplateVersionSeed::getVoucherLifespan), 60)
                .set(field(UpdateEServiceTemplateVersionSeed::getDailyCallsTotal), 10)
                .set(field(UpdateEServiceTemplateVersionSeed::getDailyCallsPerConsumer), 1)
                .set(field(UpdateEServiceTemplateVersionSeed::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .set(
                        field(UpdateEServiceTemplateVersionSeed::getAttributes),
                        new EServiceTemplateAttributesSeed()
                                .certified(List.of())
                                .declared(List.of())
                                .verified(List.of())
                )
                .ignore(field(UpdateEServiceTemplateVersionSeed::getAsyncExchangeProperties))
                .create();
    }

    @Override
    public EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand() {
        return BffEServiceTemplateCreationCommand.from(creationRequest());
    }

    @Override
    public UpdateEServiceTemplateCommand defaultUpdateEServiceTemplateCommand() {
        return BffUpdateEServiceTemplateCommand.from(updateRequest());
    }

    @Override
    public UpdateEServiceTemplateNameCommand defaultUpdateTemplateNameCommand() {
        return BffUpdateEServiceTemplateNameCommand.from(updateNameRequest());
    }

    @Override
    public UpdateEServiceTemplateIntendedTargetCommand defaultUpdateTemplateIntendedTargetCommand() {
        return BffUpdateEServiceTemplateIntendedTargetCommand.from(updateIntendedTargetRequest());
    }

    @Override
    public UpdateEServiceTemplateDescriptionCommand defaultUpdateTemplateDescriptionCommand() {
        return BffUpdateEServiceTemplateDescriptionCommand.from(updateDescriptionRequest());
    }

    @Override
    public UpdateEServiceTemplateVersionCommand defaultUpdateTemplateVersionCommand() {
        return BffUpdateEServiceTemplateVersionCommand.from(updateDraftVersionRequest());
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private VersionSeedForEServiceTemplateCreation defaultVersionSeed() {
        return Instancio.of(VersionSeedForEServiceTemplateCreation.class)
                .set(field(VersionSeedForEServiceTemplateCreation::getDescription), randomAlphanumericName("version-description", 35))
                .set(field(VersionSeedForEServiceTemplateCreation::getVoucherLifespan), 60)
                .set(field(VersionSeedForEServiceTemplateCreation::getDailyCallsTotal), 10)
                .set(field(VersionSeedForEServiceTemplateCreation::getDailyCallsPerConsumer), 1)
                .set(field(VersionSeedForEServiceTemplateCreation::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .create();
    }
}


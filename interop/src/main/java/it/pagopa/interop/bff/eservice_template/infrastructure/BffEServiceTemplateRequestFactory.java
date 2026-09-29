package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.bff.eservice_template.application.BffUpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.command.UpdateEServiceTemplateVersionCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.instancio.Select.field;

@Component
public class BffEServiceTemplateRequestFactory implements EServiceTemplateRequestFactory {

    @Override
    public EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand() {
        VersionSeedForEServiceTemplateCreation versionSeed = Instancio.of(VersionSeedForEServiceTemplateCreation.class)
                .generate(field(VersionSeedForEServiceTemplateCreation::getDescription), gen -> gen.string().prefix("version-description-").length(15))
                .set(field(VersionSeedForEServiceTemplateCreation::getVoucherLifespan), 60)
                .set(field(VersionSeedForEServiceTemplateCreation::getDailyCallsTotal), 10)
                .set(field(VersionSeedForEServiceTemplateCreation::getDailyCallsPerConsumer), 1)
                .set(field(VersionSeedForEServiceTemplateCreation::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .create();

        EServiceTemplateSeed creationSeed = Instancio.of(EServiceTemplateSeed.class)
                .generate(field(EServiceTemplateSeed::getName), gen -> gen.string().prefix("eservice-template-").length(15))
                .generate(field(EServiceTemplateSeed::getDescription), gen -> gen.string().prefix("description-").length(15))
                .generate(field(EServiceTemplateSeed::getIntendedTarget), gen -> gen.string().prefix("intended-target-").length(15))
                .set(field(EServiceTemplateSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceTemplateSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceTemplateSeed::getPersonalData), false)
                .set(field(EServiceTemplateSeed::getAsyncExchange), false)
                .set(field(EServiceTemplateSeed::getIsSignalHubEnabled), false)
                .set(field(EServiceTemplateSeed::getVersion), versionSeed)
                .create();

        return BffEServiceTemplateCreationCommand.from(creationSeed);
    }

    @Override
    public UpdateEServiceTemplateVersionCommand defaultUpdateVersionCommand() {
        var payload = Instancio.of(UpdateEServiceTemplateVersionSeed.class)
                .set(field(UpdateEServiceTemplateVersionSeed::getVoucherLifespan), 60)
                .set(field(UpdateEServiceTemplateVersionSeed::getDailyCallsTotal), 10)
                .set(field(UpdateEServiceTemplateVersionSeed::getDailyCallsPerConsumer), 1)
                .set(field(UpdateEServiceTemplateVersionSeed::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .set(field(UpdateEServiceTemplateVersionSeed::getDescription), "default description")
                .set(field(UpdateEServiceTemplateVersionSeed::getAttributes),
                        new EServiceTemplateAttributesSeed()
                                .certified(new ArrayList<>())
                                .declared(new ArrayList<>())
                                .verified(new ArrayList<>())
                )
                .ignore(field(UpdateEServiceTemplateVersionSeed::getAsyncExchangeProperties))
                .create();

        return BffUpdateEServiceTemplateVersionCommand.from(payload);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}


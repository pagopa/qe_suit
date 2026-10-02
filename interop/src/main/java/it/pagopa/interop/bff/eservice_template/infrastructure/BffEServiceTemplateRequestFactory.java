package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
public class BffEServiceTemplateRequestFactory implements EServiceTemplateRequestFactory {

    @Override
    public EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand() {
        VersionSeedForEServiceTemplateCreation versionSeed = new VersionSeedForEServiceTemplateCreation()
                .description("default template version description")
                .voucherLifespan(60)
                .dailyCallsPerConsumer(1)
                .dailyCallsTotal(10)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);

        EServiceTemplateSeed creationSeed = Instancio.of(EServiceTemplateSeed.class)
                .generate(field(EServiceTemplateSeed::getName), gen -> gen.string().prefix("template-").length(15))
                .generate(field(EServiceTemplateSeed::getDescription), gen -> gen.string().prefix("description-").length(15))
                .generate(field(EServiceTemplateSeed::getIntendedTarget), gen -> gen.string().prefix("target-").length(15))
                .set(field(EServiceTemplateSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceTemplateSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceTemplateSeed::getIsSignalHubEnabled), false)
                .set(field(EServiceTemplateSeed::getPersonalData), false)
                .set(field(EServiceTemplateSeed::getAsyncExchange), false)
                .set(field(EServiceTemplateSeed::getVersion), versionSeed)
                .create();

        return BffEServiceTemplateCreationCommand.from(creationSeed);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}


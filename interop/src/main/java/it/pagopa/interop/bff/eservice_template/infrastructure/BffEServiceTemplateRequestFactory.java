package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateCreationCommand;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplatePreparationConfiguration;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.utils.RandomUtils;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static org.instancio.Select.fields;

@Component
public class BffEServiceTemplateRequestFactory implements EServiceTemplateRequestFactory {
    @Override
    public BffEServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand() {
        EServiceTemplateSeed seed = Instancio.of(EServiceTemplateSeed.class)
                .ignore(fields(field -> true))
                .create();
        BffEServiceTemplateCreationCommand command = BffEServiceTemplateCreationCommand.from(seed);
        command.name(RandomUtils.randomAlphanumericName("template"));
        command.description("Template e-service per test automatici");
        command.intendedTarget("Enti aderenti alla piattaforma Interop");
        command.technology(EServiceTechnology.REST);
        command.mode(EServiceMode.DELIVER);
        command.handlePersonalData(false);
        command.isSignalHubEnabled(false);
        command.isAsync(false);
        command.versionDescription("Prima versione del template di test");
        command.voucherLifespan(60);
        command.dailyCallsPerConsumer(1);
        command.dailyCallsTotal(10);
        command.agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);
        return command;
    }

    @Override
    public EServiceTemplateCreationCommand defaultCreationEServiceTemplateCommand(EServiceTemplatePreparationConfiguration configuration) {
        var command = defaultCreationEServiceTemplateCommand();
        command.technology(configuration.getTechnology());
        command.interfaceDocumentPath(configuration.getInterfaceFixture());

        return command;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

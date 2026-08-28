package it.pagopa.interop.web.eservice.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.web.eservice.application.WebEServiceCreationCommand;
import it.pagopa.interop.web.eservice.application.WebUpdateEServiceDescriptorCommand;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.instancio.Select.field;

@Component
public class WebEServiceRequestFactory implements EServiceRequestFactory {
    @Override
    public EServiceCreationCommand defaultCreationEServiceCommand() {
        EServiceSeed creationSeed = Instancio.of(EServiceSeed.class)
                .generate(field(EServiceSeed::getName), gen -> gen.string().prefix("eservice-").length(15))
                .generate(field(EServiceSeed::getDescription), gen -> gen.string().prefix("description-").length(15))
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceSeed::getIsConsumerDelegable), true)
                .set(field(EServiceSeed::getIsClientAccessDelegable), true)
                .set(field(EServiceSeed::getPersonalData), false)
                .set(field(EServiceSeed::getAsyncExchange), false)
                .set(field(EServiceSeed::getIsSignalHubEnabled), false)
                .create();

        return WebEServiceCreationCommand.from(creationSeed);
    }

    @Override
    public UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand() {
        var payload = Instancio.of(UpdateEServiceDescriptorSeed.class)
                .set(field(UpdateEServiceDescriptorSeed::getVoucherLifespan), 60)
                .set(field(UpdateEServiceDescriptorSeed::getDailyCallsTotal), 10)
                .set(field(UpdateEServiceDescriptorSeed::getDailyCallsPerConsumer), 1)
                .set(field(UpdateEServiceDescriptorSeed::getAudience), List.of("QA"))
                .set(field(UpdateEServiceDescriptorSeed::getAgreementApprovalPolicy), AgreementApprovalPolicy.AUTOMATIC)
                .set(field(UpdateEServiceDescriptorSeed::getDescription), "default description")
                .set(field(UpdateEServiceDescriptorSeed::getAttributes),
                        new DescriptorAttributesSeed()
                                .certified(List.of())
                                .declared(List.of())
                                .verified(List.of())
                )
                .ignore(field(UpdateEServiceDescriptorSeed::getAsyncExchangeProperties))
                .create();

        return WebUpdateEServiceDescriptorCommand.from(payload);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}

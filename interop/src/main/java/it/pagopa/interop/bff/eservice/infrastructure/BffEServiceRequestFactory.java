package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.instancio.Instancio;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
public class BffEServiceRequestFactory implements EServiceRequestFactory {

    public EServiceSeed creationRequest() {
        return defaultEServiceSeed();
    }

    public UpdateEServiceSeed updateRequest() {
        EServiceSeed seed = defaultEServiceSeed();
        return Instancio.of(UpdateEServiceSeed.class)
                .set(field(UpdateEServiceSeed::getName), seed.getName())
                .set(field(UpdateEServiceSeed::getDescription), seed.getDescription())
                .set(field(UpdateEServiceSeed::getTechnology), seed.getTechnology())
                .set(field(UpdateEServiceSeed::getMode), seed.getMode())
                .set(field(UpdateEServiceSeed::getIsSignalHubEnabled), seed.getIsSignalHubEnabled())
                .set(field(UpdateEServiceSeed::getIsConsumerDelegable), seed.getIsConsumerDelegable())
                .set(field(UpdateEServiceSeed::getIsClientAccessDelegable), seed.getIsClientAccessDelegable())
                .set(field(UpdateEServiceSeed::getPersonalData), seed.getPersonalData())
                .set(field(UpdateEServiceSeed::getAsyncExchange), seed.getAsyncExchange())
                .create();
    }

    public EServiceNameUpdateSeed updateNameRequest() {
        return Instancio.of(EServiceNameUpdateSeed.class)
                .set(field(EServiceNameUpdateSeed::getName), randomAlphanumericName("eservice-name", 20))
                .create();
    }

    public EServiceDescriptionUpdateSeed updateDescriptionRequest() {
        return Instancio.of(EServiceDescriptionUpdateSeed.class)
                .set(field(EServiceDescriptionUpdateSeed::getDescription), randomAlphanumericName("description", 40))
                .create();
    }

    @Override
    public EServiceCreationCommand defaultCreationEServiceCommand() {
        return BffEServiceCreationCommand.from(defaultEServiceSeed());
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

        return BffUpdateEServiceDescriptorCommand.from(payload);
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private EServiceSeed defaultEServiceSeed() {
        return Instancio.of(EServiceSeed.class)
                .set(field(EServiceSeed::getName), randomAlphanumericName("eservice", 15))
                .set(field(EServiceSeed::getDescription), randomAlphanumericName("description", 20))
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceSeed::getIsConsumerDelegable), true)
                .set(field(EServiceSeed::getIsClientAccessDelegable), true)
                .set(field(EServiceSeed::getPersonalData), false)
                .set(field(EServiceSeed::getAsyncExchange), false)
                .set(field(EServiceSeed::getIsSignalHubEnabled), false)
                .create();
    }
}

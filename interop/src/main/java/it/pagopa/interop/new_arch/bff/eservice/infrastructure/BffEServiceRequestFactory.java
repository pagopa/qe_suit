package it.pagopa.interop.new_arch.bff.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorQuotas;
import it.pagopa.interop.new_arch.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.new_arch.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
public class BffEServiceRequestFactory implements EServiceRequestFactory {

    @Override
    public EServiceCreationCommand defaultCreationEServiceCommand() {
        EServiceSeed creationSeed = Instancio.of(EServiceSeed.class)
                .generate(field(EServiceSeed::getName), gen -> gen.string().prefix("eservice-").length(15))
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.DELIVER)
                .set(field(EServiceSeed::getIsConsumerDelegable), true)
                .set(field(EServiceSeed::getIsClientAccessDelegable), true)
                .set(field(EServiceSeed::getPersonalData), false)
                .set(field(EServiceSeed::getAsyncExchange), false)
                .set(field(EServiceSeed::getIsSignalHubEnabled), false)
                .create();

        return BffEServiceCreationCommand.from(creationSeed);
    }

    @Override
    public UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand() {
        var payload = Instancio.of(UpdateEServiceDescriptorQuotas.class)
                .set(field(UpdateEServiceDescriptorQuotas::getVoucherLifespan), 60)
                .set(field(UpdateEServiceDescriptorQuotas::getDailyCallsTotal), 10)
                .set(field(UpdateEServiceDescriptorQuotas::getDailyCallsPerConsumer), 1)
                .ignore(field(UpdateEServiceDescriptorQuotas::getAttributes))
                .create();

        return BffUpdateEServiceDescriptorCommand.from(payload);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

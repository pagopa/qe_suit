package it.pagopa.interop.bff.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.bff.service.EServiceBffService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.cucumber.context.ChannelContext;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceBffController  {

    private final EServiceBffService service;
    private final ChannelContext channelContext;

    @Given("un EService in stato DRAFT creato mediante API BFF avente tutti i campi compilati, tra cui:")
    public void createWithOverride(EServiceSeed seed) {
        channelContext.setCurrentChannel(Channel.BFF);
        service
            .createWithOverride(seed)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }

    @Given("un EService in stato DRAFT creato mediante API BFF con:")
    public void createWith(EServiceSeed seed) {
        channelContext.setCurrentChannel(Channel.BFF);
        service
            .createWith(seed)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }

}

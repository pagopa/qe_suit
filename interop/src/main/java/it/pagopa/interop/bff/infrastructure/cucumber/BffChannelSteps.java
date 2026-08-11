package it.pagopa.interop.bff.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BffChannelSteps {

    private final ChannelContext channelContext;

    @Given("una sessione HTTP programmatica su BFF")
    public void setBffChannel(){
        log.debug("Inizializzazione canale BFF");
        channelContext.setCurrentChannel(Channel.BFF);
    }
}

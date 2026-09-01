package it.pagopa.interop.bff.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BffChannelSteps {

    private final CurrentChannel<Channel> currentChannel;

    @Given("una sessione HTTP programmatica su BFF")
    public void setBffChannel(){
        log.debug("Inizializzazione canale BFF");
        currentChannel.setCurrentChannel(Channel.BFF);
    }
}

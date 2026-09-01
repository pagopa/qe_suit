package it.pagopa.send.b2b.delivery.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.send.common.kernel.context.CurrentChannel;
import it.pagopa.send.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class B2BChannelSteps {

    private final CurrentChannel currentChannel;

    @Given("una sessione HTTP programmatica su B2B")
    public void setB2BChannel() {
        log.debug("Inizializzazione canale B2B");
        currentChannel.setCurrentChannel(Channel.B2B);
    }
}

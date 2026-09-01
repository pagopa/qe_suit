package it.pagopa.send.web.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.kernel.context.CurrentChannel;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WebChannelStep {
    private final CurrentChannel currentChannel;
    private final CurrentUserSession currentUserSession;
    private final WebBrowserContext webBrowserContext;

    @Given("la PA {tenant} è loggata al portale SEND tramite Browser")
    public void setWebChannel(Tenant tenant) {
        log.debug("Inizializzazione canale Web");
        webBrowserContext.setCurrentUser(tenant);
        currentUserSession.setSender(tenant);
        currentChannel.setCurrentChannel(Channel.WEB_BROWSER);
    }
}

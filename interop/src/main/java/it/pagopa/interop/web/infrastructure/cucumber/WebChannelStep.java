package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.common.infrastructure.cucumber.context.UserContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WebChannelStep {

    private final ChannelContext channelContext;
    private final UserContext userContext;

    @Given("una sessione attiva nel Browser/browser sul portale Interop")
    @Given("l'utente si collega al portale Interop dal Browser/browser")
    public void setWebChannel(){
        log.debug("Inizializzazione canale Web");
        channelContext.setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Given("l'utente {userRole} di {tenant} si collega al portale Interop dal Browser/browser")
    @Given("un( utente) {userRole} di {tenant} collegato al portale Interop dal Browser/browser")
    public void setWebChannel(UserRole userRole, Tenant tenant){
        log.debug("Inizializzazione canale Web");
        userContext.set(User.getTenantUser(tenant, userRole), tenant);
        channelContext.setCurrentChannel(Channel.WEB_BROWSER);
    }
}

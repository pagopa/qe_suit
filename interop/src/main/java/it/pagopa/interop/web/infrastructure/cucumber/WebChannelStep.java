package it.pagopa.interop.web.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WebChannelStep {

    private final CurrentChannel currentChannel;
    private final CurrentUserSession currentUserSession;

    @Given("una sessione attiva nel Browser/browser sul portale Interop")
    @Given("l'utente si collega al portale Interop dal Browser/browser")
    public void setWebChannel() {
        log.debug("Inizializzazione canale Web");
        currentChannel.setCurrentChannel(Channel.WEB_BROWSER);
    }

    @Given("l'utente {userRole} di {tenant} si collega al portale Interop dal Browser/browser")
    @Given("un( utente) {userRole} di/del {tenant} collegato al portale Interop dal Browser/browser")
    public void setWebChannel(UserRole userRole, Tenant tenant) {
        log.debug("Inizializzazione canale Web");
        currentUserSession.set(User.getTenantUser(tenant, userRole), tenant);
        currentChannel.setCurrentChannel(Channel.WEB_BROWSER);
    }
}

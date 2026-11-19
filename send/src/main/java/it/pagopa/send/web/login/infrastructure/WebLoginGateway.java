package it.pagopa.send.web.login.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.login.application.LoginGateway;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.infrastructure.page.ConfigureAddressSendPage;
import it.pagopa.send.web.login.infrastructure.page.OneIdPage;
import it.pagopa.send.web.login.infrastructure.page.PgLoginPage;
import it.pagopa.send.web.login.infrastructure.page.component.PortalLogout;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.PtInstitutionsPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Accesso e sessione sul browser. Il login passa da {@link LoginService}; il portale è quello del tipo di utente della
 * sessione, cioè il BackOffice PT per il PT, il portale ente mittente per la PA e il portale imprese per la PG. Dopo la
 * disconnessione PT e PA tornano al login di SelfCare, le imprese a quello del portale imprese.
 */
@Service
@RequiredArgsConstructor
public class WebLoginGateway implements LoginGateway {
    private final WebPresentationGateway browser;
    private final LoginService loginService;
    private final CurrentUserSession currentUserSession;

    @Override
    public void loginAsTenant(User tenant) {
        loginService.loginAsTenant(tenant);
    }

    @Override
    public void loginAsRecipient(String recipientType, User recipient) {
        loginService.loginAsRecipient(recipientType, recipient);
    }

    @Override
    public void forceTenantSession(User tenant) {
        loginService.forceTenantSession(tenant);
    }

    @Override
    public void forceRecipientSession(String recipientType, User recipient) {
        loginService.forceRecipientSession(recipientType, recipient);
    }

    // al primo accesso i portali dei destinatari possono mostrare la configurazione di SEND prima delle notifiche
    @Override
    public void skipSendConfiguration() {
        ConfigureAddressSendPage configureAddressSendPage = browser.bind(ConfigureAddressSendPage.class);
        try {
            configureAddressSendPage.assertLoaded();
        } catch (AssertionError e) {
            return;
        }
        configureAddressSendPage.clickSkipConfigButton();
    }

    @Override
    public void verifyPortalAccess() {
        switch (currentUserType()) {
            case PT -> browser.bind(PtInstitutionsPage.class).assertLoaded();
            case PA -> browser.bind(DashboardPage.class).assertLoaded();
            case PG -> browser.bind(NotificationPage.class).assertLoaded();
            default -> throw unsupportedUserType(currentUserType());
        }
    }

    @Override
    public void logout() {
        browser.bind(PortalLogout.class).logout();
        // la sessione è chiusa: senza utente le navigazioni successive (es. un nuovo accesso) non ne riscrivono il token
        currentUserSession.clear();
    }

    @Override
    public void verifySessionEnded(UserType portalUserType) {
        switch (portalUserType) {
            case PT, PA -> browser.bind(OneIdPage.class).assertLoaded();
            case PG -> browser.bind(PgLoginPage.class).assertLoaded();
            default -> throw unsupportedUserType(portalUserType);
        }
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    private UserType currentUserType() {
        return currentUserSession.getUser().getType();
    }

    private UnsupportedOperationException unsupportedUserType(UserType userType) {
        return new UnsupportedOperationException("Sessione non gestita sul canale Web per il tipo di utente " + userType);
    }
}

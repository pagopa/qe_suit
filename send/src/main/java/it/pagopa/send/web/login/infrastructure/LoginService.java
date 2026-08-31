package it.pagopa.send.web.login.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.kernel.domain.Recipient;
import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.common.kernel.domain.User;
import it.pagopa.send.domain.web.commons.pages.login.AbstractOneIdPage;
import it.pagopa.send.domain.web.commons.pages.login.OneIdPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.login.PfLoginPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.NotificationPage;
import it.pagopa.send.domain.web.pages.destinatario.pg.login.PgLoginPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.domain.web.pages.mittente.login.TenantSelectionPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Azioni di login sul browser: nessun servizio esterno coinvolto, solo interazione con le pagine.
 * Due modalità: {@code loginAs*} attraversa l'intero flusso SPID; {@code force*Session} salta le
 * pagine di login e forza direttamente una sessione self-care nel sessionStorage, atterrando sulla
 * pagina finale (dashboard/notifiche) senza passare da OneIdPage/PgLoginPage/PfLoginPage.
 */
@Component
@RequiredArgsConstructor
public class LoginService {
    private static final String SESSION_STORAGE_KEY = "user";
    private static final String SESSION_TOKEN_PROPERTY = "token.session.%s";

    private final WebPresentationGateway uiGateway;
    private final WebBrowserContext webBrowserContext;
    private final TenantSelectionPage tenantSelectionPage;
    private final DashboardPage dashboardPage;
    private final NotificationPage notificationPage;
    private final NotificationPFPage notificationPFPage;
    private final SelfCareSessionPayloadFactory sessionPayloadFactory;
    private final Environment environment;

    public void loginAsTenant(Tenant tenant) {
        OneIdPage loginPage = uiGateway.bind(OneIdPage.class);
        loginPage.navigateTo();
        loginPage.loginWithSpid(tenant);
        tenantSelectionPage.selectComune(tenant.getOrganization());

        webBrowserContext.setTenant(tenant);
        webBrowserContext.setCurrentUser(tenant);
    }

    public void loginAsRecipient(String userType, Recipient recipient) {
        AbstractOneIdPage loginPage = switch (userType) {
            case "PG" -> uiGateway.bind(PgLoginPage.class);
            default -> uiGateway.bind(PfLoginPage.class);
        };
        loginPage.navigateTo();
        loginPage.loginWithSpid(recipient);

        webBrowserContext.setRecipient(recipient);
        webBrowserContext.setCurrentUser(recipient);
    }

    public void forceTenantSession(Tenant tenant) {
        String payload = sessionPayloadFactory.buildForTenant(tenant, sessionTokenFor(tenant));
        forceSession(payload, dashboardPage);

        webBrowserContext.setTenant(tenant);
        webBrowserContext.setCurrentUser(tenant);
    }

    public void forceRecipientSession(String userType, Recipient recipient) {
        String payload = sessionPayloadFactory.buildForRecipient(recipient, sessionTokenFor(recipient));
        Page landingPage = "PG".equals(userType) ? notificationPage : notificationPFPage;
        forceSession(payload, landingPage);

        webBrowserContext.setRecipient(recipient);
        webBrowserContext.setCurrentUser(recipient);
    }

    private String sessionTokenFor(User user) {
        return environment.getProperty(SESSION_TOKEN_PROPERTY.formatted(user.getUsername().toLowerCase()));
    }

    private void forceSession(String sessionPayload, Page landingPage) {
        landingPage.navigateTo();
        uiGateway.setSessionStorageItem(SESSION_STORAGE_KEY, sessionPayload);


        landingPage.navigateTo();
        landingPage.assertLoaded();
    }
}

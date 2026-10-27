package it.pagopa.send.web.login.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.web.login.infrastructure.page.AbstractOneIdPage;
import it.pagopa.send.web.login.infrastructure.page.OneIdPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.login.infrastructure.page.PfLoginPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.login.infrastructure.page.PgLoginPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.login.infrastructure.page.TenantSelectionPage;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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

    private final WebPresentationGateway uiGateway;
    private final CurrentUserSession currentUserSession;
    private final TenantSelectionPage tenantSelectionPage;
    private final DashboardPage dashboardPage;
    private final NotificationPage notificationPage;
    private final NotificationPFPage notificationPFPage;
    private final SelfCareSessionPayloadFactory sessionPayloadFactory;
    private final DynamicUserTokenProvider tokenProvider;

    public void loginAsTenant(User tenant) {
        OneIdPage loginPage = uiGateway.bind(OneIdPage.class);
        loginPage.navigateTo();
        loginPage.loginWithSpid(tenant);
        tenantSelectionPage.selectComune(tenant.getOrganization());

        currentUserSession.setSender(tenant);
    }

    public void loginAsRecipient(String userType, User recipient) {
        AbstractOneIdPage loginPage = switch (userType) {
            case "PG" -> uiGateway.bind(PgLoginPage.class);
            default -> uiGateway.bind(PfLoginPage.class);
        };
        loginPage.navigateTo();
        loginPage.loginWithSpid(recipient);

        setRecipient(recipient);
    }

    public void forceTenantSession(User tenant) {
        // prima di navigare: ogni navigazione riscrive il sessionStorage con l'attore corrente della sessione
        currentUserSession.setSender(tenant);

        String payload = sessionPayloadFactory.build(tenant, sessionTokenFor(tenant));
        forceSession(payload, dashboardPage);
    }

    public void forceRecipientSession(String userType, User recipient) {
        String payload = sessionPayloadFactory.build(recipient, sessionTokenFor(recipient));
        Page landingPage = "PG".equals(userType) ? notificationPage : notificationPFPage;
        setRecipient(recipient);
        forceSession(payload, landingPage);
    }

    // il destinatario diventa l'unico attore: un mittente rimasto da step precedenti avrebbe la precedenza
    private void setRecipient(User recipient) {
        currentUserSession.setSender(null);
        currentUserSession.setRecipients(List.of(recipient));
    }

    private String sessionTokenFor(User user) {
        return tokenProvider.requireToken(user.getUsername());
    }

    private void forceSession(String sessionPayload, Page landingPage) {
        landingPage.navigateTo();
        uiGateway.setSessionStorageItem(SESSION_STORAGE_KEY, sessionPayload);

        landingPage.navigateTo();
        landingPage.assertLoaded();
    }
}

package it.pagopa.send.common.login.application;

import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Casi d'uso di accesso e sessione: login con autenticazione SPID o sessione forzata, accesso alle funzionalità del
 * portale, disconnessione e verifica della sessione terminata. Puro pass-through verso {@link LoginGateway}: sono le
 * implementazioni del gateway (una per canale) a sapere come procedere.
 */
@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final LoginGateway loginGateway;

    public void loginAsTenant(User tenant) {
        loginGateway.loginAsTenant(tenant);
    }

    public void loginAsRecipient(String recipientType, User recipient) {
        loginGateway.loginAsRecipient(recipientType, recipient);
    }

    public void forceTenantSession(User tenant) {
        loginGateway.forceTenantSession(tenant);
    }

    public void forceRecipientSession(String recipientType, User recipient) {
        loginGateway.forceRecipientSession(recipientType, recipient);
    }

    public void skipSendConfiguration() {
        loginGateway.skipSendConfiguration();
    }

    public void verifyPortalAccess() {
        loginGateway.verifyPortalAccess();
    }

    public void logout() {
        loginGateway.logout();
    }

    public void verifySessionEnded() {
        loginGateway.verifySessionEnded();
    }
}

package it.pagopa.send.common.login.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;
import org.springframework.plugin.core.Plugin;

/**
 * Accesso ai portali SEND e sessione dell'utente dello scenario: ogni implementazione sa come autenticare l'utente sul
 * proprio canale, come verificare che abbia accesso alle funzionalità del suo portale, come chiudere la sessione e come
 * verificare che sia terminata.
 */
public interface LoginGateway extends Plugin<Channel> {
    void loginAsTenant(User tenant);

    void loginAsRecipient(String recipientType, User recipient);

    void forceTenantSession(User tenant);

    void forceRecipientSession(String recipientType, User recipient);

    /**
     * Il destinatario salta la configurazione del prodotto SEND proposta al primo accesso, che risulta così fatta: sul
     * Web si preme "Salta" se la pagina è mostrata, su un canale API si invoca la chiamata che la segna come fatta.
     */
    void skipSendConfiguration();

    void verifyPortalAccess();

    void logout();

    /** Verifica che la sessione sul portale del tipo di utente indicato (PT, PA o PG) sia terminata. */
    void verifySessionEnded(UserType portalUserType);
}

package it.pagopa.send.common.kernel.context;

import it.pagopa.send.common.user.domain.User;

/**
 * Utente con cui opera lo scenario: lo impostano login, step utente, journey e contract
 * validator; lo leggono gli adapter di ogni canale (sessione del browser, api-key delle chiamate B2B,
 * pagine PA/PF/PG). Mittente e destinatari di una notifica sono dati della notifica, non della sessione.
 */
public interface CurrentUserSession {
    void setUser(User user);

    User getUser();

    /**
     * {@code false} quando nessun utente è impostato: navigazione anonima, es. i link con token già
     * valorizzato usati dal portale di supporto.
     */
    boolean hasUser();
}

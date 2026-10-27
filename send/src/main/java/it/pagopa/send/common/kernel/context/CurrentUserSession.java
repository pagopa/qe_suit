package it.pagopa.send.common.kernel.context;

import it.pagopa.send.common.user.domain.User;

import java.util.List;

public interface CurrentUserSession {
    void setSender(User sender);

    User getSender();

    void setRecipients(List<User> recipients);

    List<User> getRecipients();

    /**
     * Attore dello scenario: il sender se impostato, altrimenti il primo destinatario, altrimenti
     * {@code null} (nessun utente da impersonare: navigazione diretta senza faking di sessione, es. i
     * link con token già valorizzato usati dal portale di supporto). Usato dagli adapter web, sia nei
     * test di contratto sia negli scenari Cucumber, per autenticare il browser e per scegliere le
     * pagine in base al tipo di utente (PA, PF, PG).
     */
    User getCurrentActor();
}

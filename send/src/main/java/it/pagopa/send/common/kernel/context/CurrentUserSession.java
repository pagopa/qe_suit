package it.pagopa.send.common.kernel.context;

import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.user.domain.User;

import java.util.List;

public interface CurrentUserSession {
    void setSender(Tenant sender);

    Tenant getSender();

    void setRecipients(List<Recipient> recipients);

    List<Recipient> getRecipients();

    /**
     * Attore da usare per l'autenticazione UI nei test di contratto: il sender se impostato,
     * altrimenti il primo destinatario, altrimenti {@code null} (nessun utente da impersonare:
     * navigazione diretta senza faking di sessione, es. i link con token già valorizzato usati
     * dal portale di supporto). Usato da {@code WebJUnitSuitConfig} per popolare il
     * {@code WebBrowserContext} sia negli scenari "come mittente"/"come PF/PG" sia in quelli
     * anonimi.
     */
    User getCurrentActor();
}

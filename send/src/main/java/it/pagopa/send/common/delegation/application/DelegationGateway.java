package it.pagopa.send.common.delegation.application;

import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
import org.springframework.plugin.core.Plugin;

/**
 * Ogni implementazione sa gestire le deleghe tra destinatari per il proprio canale, con le credenziali dell'utente che
 * fa l'operazione: il delegante crea la delega, il delegato la cerca e la accetta.
 */
public interface DelegationGateway extends Plugin<Channel> {

    /**
     * Indica se il delegato ha già una delega attiva o in attesa del delegante: risponde subito, anche quando non ce ne
     * sono.
     */
    boolean hasDelegation(User delegator, User delegate);

    /**
     * Delega attiva o in attesa del delegante verso il delegato, cercata tra quelle del delegato: la ricerca si ripete
     * finché la delega non compare, perché appena creata può non essere ancora restituita.
     */
    Delegation findDelegation(User delegator, User delegate);

    void createDelegation(User delegator, User delegate, DelegationCreationCommand command);

    void acceptDelegation(User delegate, Delegation delegation, String verificationCode);
}

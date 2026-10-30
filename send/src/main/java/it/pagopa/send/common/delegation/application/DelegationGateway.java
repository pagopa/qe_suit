package it.pagopa.send.common.delegation.application;

import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;

/**
 * Ogni implementazione sa gestire le deleghe tra destinatari per il proprio canale, con le credenziali dell'utente che
 * fa l'operazione: il delegante crea la delega, il delegato la cerca e la accetta.
 */
public interface DelegationGateway extends Plugin<Channel> {

    /**
     * Delega attiva o in attesa del delegante verso il delegato, cercata tra quelle del delegato.
     */
    Optional<Delegation> findDelegation(User delegator, User delegate);

    void createDelegation(User delegator, User delegate, DelegationCreationCommand command);

    void acceptDelegation(User delegate, Delegation delegation, String verificationCode);
}

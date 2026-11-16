package it.pagopa.send.common.partner_tecnologico.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.partner_tecnologico.domain.PtInstitution;
import it.pagopa.send.common.user.domain.User;
import org.springframework.plugin.core.Plugin;

import java.util.List;

/**
 * Ogni implementazione sa leggere gli enti deleganti di un Partner Tecnologico e creare una delega di un ente verso il
 * PT, per il proprio canale.
 */
public interface PtDelegationGateway extends Plugin<Channel> {

    /** Prima pagina degli enti deleganti del PT, nell'ordine in cui li restituisce SEND: risponde subito. */
    List<PtInstitution> findInstitutions(User partner, int limit);

    /** Crea, o riattiva se cancellata, la delega dell'ente {@code delegator} al PT {@code partner}. */
    void createDelegation(User delegator, User partner);

    /**
     * Attende che il PT abbia almeno un ente delegante: la delega appena creata arriva a SEND in modo asincrono, per
     * cui la lettura si ripete finché non compare.
     */
    void awaitInstitutions(User partner);
}

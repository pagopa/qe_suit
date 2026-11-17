package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.user.domain.User;

public interface PtDelegationJourney<SELF extends PtDelegationJourney<SELF>> extends JourneyModule {
    /**
     * Garantisce che il Partner Tecnologico abbia almeno un ente delegante: se non ne ha, crea la delega
     * dell'ente {@code delegator} e attende che arrivi a SEND.
     */
    SELF withPtDelegation(User delegator, User partner);
}

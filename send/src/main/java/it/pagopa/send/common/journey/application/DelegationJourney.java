package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.user.domain.User;

public interface DelegationJourney<SELF extends DelegationJourney<SELF>> extends JourneyModule {

    /**
     * Garantisce una delega attiva del delegante verso il delegato: riusa quella già presente, accettandola se è
     * ancora in attesa, altrimenti la crea e la fa accettare. La delega resta in {@code EntityStore}.
     */
    SELF withActiveDelegation(User delegator, User delegate);
}

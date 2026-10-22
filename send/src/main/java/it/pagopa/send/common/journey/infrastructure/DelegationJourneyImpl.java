package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.send.common.delegation.application.DelegationCreationCommand;
import it.pagopa.send.common.delegation.application.DelegationUseCase;
import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.delegation.infrastructure.factory.DelegationRequestFactory;
import it.pagopa.send.common.journey.application.DelegationJourney;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Composer fluente per le deleghe tra destinatari, sul modello di {@link LegalNotificationJourneyImpl}: bean singleton
 * senza stato proprio, la delega ottenuta resta in {@code EntityStore}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelegationJourneyImpl implements DelegationJourney<DelegationJourneyImpl> {

    private final DelegationUseCase delegationUseCase;
    private final DelegationRequestFactory requestFactory;

    @Override
    public DelegationJourneyImpl withActiveDelegation(User delegator, User delegate) {
        DelegationCreationCommand command = requestFactory.defaultCreation();
        Delegation delegation = delegationUseCase.findDelegation(delegator, delegate)
                .orElseGet(() -> createDelegation(delegator, delegate, command));
        delegationUseCase.acceptDelegation(delegate, delegation, command.verificationCode());
        log.info("Delega {} di {} verso {} attiva", delegation.getMandateId(), delegator.getUsername(), delegate.getUsername());
        return this;
    }

    private Delegation createDelegation(User delegator, User delegate, DelegationCreationCommand command) {
        delegationUseCase.createDelegation(delegator, delegate, command);
        return delegationUseCase.findDelegation(delegator, delegate)
                .orElseThrow(() -> new IllegalStateException("Delega creata da " + delegator.getUsername()
                        + " non trovata tra quelle di " + delegate.getUsername()));
    }
}

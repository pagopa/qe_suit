package it.pagopa.send.common.delegation.application;

import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.delegation.domain.DelegationStatus;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Casi d'uso per le deleghe tra destinatari: cercarle, crearle e accettarle. L'accettazione parte solo da una delega in
 * attesa; su una delega già attiva non fa nulla.
 */
@Service
@RequiredArgsConstructor
public class DelegationUseCase {

    private final DelegationGateway delegationGateway;

    public Optional<Delegation> findDelegation(User delegator, User delegate) {
        return delegationGateway.findDelegation(delegator, delegate);
    }

    public void createDelegation(User delegator, User delegate, DelegationCreationCommand command) {
        delegationGateway.createDelegation(delegator, delegate, command);
    }

    public void acceptDelegation(User delegate, Delegation delegation, String verificationCode) {
        if (delegation.getStatus() == DelegationStatus.ACTIVE) {
            return;
        }
        if (delegation.getStatus() != DelegationStatus.PENDING) {
            throw new IllegalStateException("La delega " + delegation.getMandateId() + " è " + delegation.getStatus()
                    + ": si può accettare solo una delega in attesa");
        }
        delegationGateway.acceptDelegation(delegate, delegation, verificationCode);
    }
}

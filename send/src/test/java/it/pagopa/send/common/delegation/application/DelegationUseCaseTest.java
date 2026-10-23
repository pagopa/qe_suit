package it.pagopa.send.common.delegation.application;

import it.pagopa.send.common.delegation.domain.Delegation;
import it.pagopa.send.common.delegation.domain.DelegationStatus;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.user.domain.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regola dell'accettazione di una delega: si accetta solo una delega in attesa, su una delega già attiva non si fa
 * nulla, negli altri stati si fallisce. Il gateway è un mock che registra le accettazioni richieste.
 */
class DelegationUseCaseTest {

    private static final String VERIFICATION_CODE = "24680";

    private final RecordingDelegationGateway gateway = new RecordingDelegationGateway();
    private final DelegationUseCase useCase = new DelegationUseCase(gateway);

    @Test
    void shouldAcceptPendingDelegation() {
        Delegation delegation = delegation(DelegationStatus.PENDING);

        useCase.acceptDelegation(null, delegation, VERIFICATION_CODE);

        assertThat(gateway.accepted).containsExactly(delegation.getMandateId());
    }

    @Test
    void shouldNotAcceptActiveDelegationAgain() {
        useCase.acceptDelegation(null, delegation(DelegationStatus.ACTIVE), VERIFICATION_CODE);

        assertThat(gateway.accepted).isEmpty();
    }

    @Test
    void shouldFailOnRevokedDelegation() {
        Delegation delegation = delegation(DelegationStatus.REVOKED);

        assertThatThrownBy(() -> useCase.acceptDelegation(null, delegation, VERIFICATION_CODE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(delegation.getMandateId());
        assertThat(gateway.accepted).isEmpty();
    }

    private static Delegation delegation(DelegationStatus status) {
        return Delegation.builder()
                .mandateId("40f54459-815d-4b54-82a0-58e1fbec0df8")
                .delegatorTaxId("BRGLRZ80D58H501Q")
                .status(status)
                .build();
    }

    private static class RecordingDelegationGateway implements DelegationGateway {

        private final List<String> accepted = new ArrayList<>();

        @Override
        public Optional<Delegation> findDelegation(User delegator, User delegate) {
            return Optional.empty();
        }

        @Override
        public void createDelegation(User delegator, User delegate, DelegationCreationCommand command) {
        }

        @Override
        public void acceptDelegation(User delegate, Delegation delegation, String verificationCode) {
            accepted.add(delegation.getMandateId());
        }

        @Override
        public boolean supports(Channel channel) {
            return true;
        }
    }
}

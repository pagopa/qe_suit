package it.pagopa.interop.common.journey.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.delegation.application.command.ConsumerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.DelegationState;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DelegationJourneySteps {

    private final InteropJourney interopJourney;

    @Given("{currentProducerDelegation} viene revocata da/dal {tenant}")
    public void revokeProducerDelegation(Delegation delegation, Tenant delegator) {
        interopJourney.revokeProducerDelegationAsDelegator(delegation.getRef(), delegator);
    }

    @Given("un e-service pubblicato da/dal {tenant} con una delega in erogazione a/al {tenant} in stato {delegationState}")
    public void createPublishedEServiceWithProducerDelegation(
            Tenant producer, Tenant delegate, DelegationState delegationState) {
        EService eService = interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
        interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                .delegator(producer)
                .delegate(delegate)
                .eServiceRef(eService.getRef())
                .targetState(delegationState)
                .build());
    }

    @Given("l'{currentEService} (è )delegato in erogazione da/dal {tenant} a/al {tenant} con una delega in stato {delegationState}")
    public void createProducerDelegation(EService eService, Tenant delegator, Tenant delegate, DelegationState delegationState) {
        interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                .delegator(delegator)
                .delegate(delegate)
                .eServiceRef(eService.getRef())
                .targetState(delegationState)
                .build());
    }

    @Given("l'{currentEService} (è )delegato in fruizione da/dal {tenant} a/al {tenant} con una delega in stato {delegationState}")
    public void createConsumerDelegation(EService eService, Tenant delegator, Tenant delegate, DelegationState delegationState) {
        interopJourney.createConsumerDelegation(ConsumerDelegationCreationCommand.builder()
                .delegator(delegator)
                .delegate(delegate)
                .eServiceRef(eService.getRef())
                .targetState(delegationState)
                .build());
    }
}

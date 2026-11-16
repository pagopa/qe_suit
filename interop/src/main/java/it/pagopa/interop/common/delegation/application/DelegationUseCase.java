package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DelegationUseCase {

    private final DelegationGateway delegationGateway;
    private final TenantUseCase tenantUseCase;
    private final CurrentUserSession currentUserSession;

    public void approveProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveProducerDelegation(delegationRef);
    }

    public Delegation createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createConsumerDelegation(delegateRef, eServiceRef);
    }

    /* TODO Possibile sovrapposizione involontaria con it.pagopa.interop.common.delegation.application.DelegationUseCase.createProducerDelegation(it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand)
    *   (per esempio a causa di lavorazioni su pr separate). Verificare che uno dei due non possa essere rimosso.
    *   createProducerDelegation(@NonNull ProducerDelegationCreationCommand command) ha in più l'abilitazione
    *   alle deleghe, e l'averla solo lì può creare ambiguità nell'utilizzo di questi metodi. */
    public Delegation createProducerDelegation(TenantRef delegateRef, EServiceRef eServiceRef) {
        return delegationGateway.createProducerDelegation(delegateRef, eServiceRef);
    }

    public Delegation createProducerDelegation(@NonNull ProducerDelegationCreationCommand command) {
        Tenant delegate = command.getDelegate();
        TenantRef delegateRef = TenantRef.of(delegate.getOrganizationId());
        currentUserSession.set(User.getTenantAdmin(delegate), delegate);
        tenantUseCase.enableProducerDelegationAvailability(delegateRef);

        Tenant delegator = command.getDelegator();
        currentUserSession.set(User.getTenantAdmin(delegator), delegator);
        Delegation delegation = createProducerDelegation(delegateRef, command.getEServiceRef());

        return switch (command.getTargetState()) {
            case WAITING_FOR_APPROVAL -> delegation;
            case ACTIVE -> {
                currentUserSession.set(User.getTenantAdmin(delegate), delegate);
                approveProducerDelegation(delegation.getRef());
                yield delegation;
            }
        };
    }

    public ProducerDelegation createProducerDelegation(
            TenantRef delegatorRef,
            TenantRef delegateRef,
            EService eService
    ) {
        if (!delegatorRef.id().toString().equals(eService.getProducerId())) {
            throw new IllegalArgumentException(
                    "L'e-service " + eService.getId() + " appartiene al delegante "
                            + eService.getProducerId() + ", non a " + delegatorRef.id()
            );
        }
        return delegationGateway.createProducerDelegation(delegatorRef, delegateRef, eService.getRef());
    }

    public void approveConsumerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveConsumerDelegation(delegationRef);
    }

    public void rejectProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.rejectProducerDelegation(delegationRef);
    }

    public void revokeProducerDelegation(DelegationRef delegationRef) {
        delegationGateway.revokeProducerDelegation(delegationRef);
    }

    public ProducerDelegation getProducerDelegation(DelegationRef delegationRef) {
        return delegationGateway.getProducerDelegation(delegationRef);
    }

    public ProducerDelegation waitForProducerDelegationState(
            DelegationRef delegationRef,
            ProducerDelegationState expectedState
    ) {
        return delegationGateway.waitForProducerDelegationState(delegationRef, expectedState);
    }
}

package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.Delegation;
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

    public void approveConsumerDelegation(DelegationRef delegationRef) {
        delegationGateway.approveConsumerDelegation(delegationRef);
    }
}

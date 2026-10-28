package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegation;
import it.pagopa.interop.common.delegation.domain.ProducerDelegationState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import org.springframework.plugin.core.Plugin;


public interface DelegationGateway extends Plugin<Channel> {

    Delegation createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    Delegation createProducerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    ProducerDelegation createProducerDelegation(
            TenantRef delegatorRef,
            TenantRef delegateRef,
            EServiceRef eServiceRef
    );

    void approveConsumerDelegation(DelegationRef delegationRef);

    void approveProducerDelegation(DelegationRef delegationRef);

    void rejectProducerDelegation(DelegationRef delegationRef);

    void revokeProducerDelegation(DelegationRef delegationRef);

    ProducerDelegation getProducerDelegation(DelegationRef delegationRef);

    ProducerDelegation waitForProducerDelegationState(
            DelegationRef delegationRef,
            ProducerDelegationState expectedState
    );
}

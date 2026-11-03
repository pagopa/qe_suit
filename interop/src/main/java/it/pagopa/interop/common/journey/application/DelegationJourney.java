package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;

public interface DelegationJourney<SELF extends DelegationJourney<SELF>> extends JourneyModule {

    SELF createProducerDelegation(ProducerDelegationCreationCommand command);

    SELF createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    SELF createProducerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    SELF createProducerDelegation(TenantRef delegateRef);

    SELF approveConsumerDelegation(DelegationRef delegationRef);
}

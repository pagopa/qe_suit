package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;

public interface DelegationJourney<SELF extends DelegationJourney<SELF>> extends JourneyModule {

    SELF createActiveProducerDelegation(Tenant delegator, Tenant delegate, EServiceRef eServiceRef);

    SELF createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    SELF approveConsumerDelegation(DelegationRef delegationRef);
}

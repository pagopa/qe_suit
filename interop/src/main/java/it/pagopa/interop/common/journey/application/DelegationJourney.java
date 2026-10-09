package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;

import java.util.UUID;

public interface DelegationJourney<SELF extends DelegationJourney<SELF>> extends JourneyModule {

    SELF createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    SELF approveConsumerDelegation(DelegationRef delegationRef);
}

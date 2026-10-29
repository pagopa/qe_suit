package it.pagopa.interop.common.delegation.application.command;

import it.pagopa.interop.common.delegation.domain.DelegationState;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProducerDelegationCreationCommand {
    @NonNull Tenant delegator;
    @NonNull Tenant delegate;
    @NonNull EServiceRef eServiceRef;
    @NonNull DelegationState targetState;
}

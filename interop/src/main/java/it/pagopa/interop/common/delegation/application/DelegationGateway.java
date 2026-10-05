package it.pagopa.interop.common.delegation.application;

import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface DelegationGateway extends Plugin<Channel> {

    Delegation createConsumerDelegation(TenantRef delegateRef, EServiceRef eServiceRef);

    void approveConsumerDelegation(UUID delegationId);
}

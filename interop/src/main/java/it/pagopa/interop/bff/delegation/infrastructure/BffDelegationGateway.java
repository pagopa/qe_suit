package it.pagopa.interop.bff.delegation.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.delegation.application.DelegationGateway;
import it.pagopa.interop.common.delegation.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BffDelegationGateway implements DelegationGateway {

    private final BffDelegationRestClient restClient;
    private final BffDelegationMapper mapper;

    @Override
    public Delegation createConsumerDelegation(@NonNull TenantRef delegateRef, @NonNull EServiceRef eServiceRef) {
        return restClient.createConsumerDelegation(delegateRef.id(), eServiceRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(consumerDelegation -> mapper.toDelegation(consumerDelegation))
                .updateContext()
                .get();
    }

    @Override
    public void approveConsumerDelegation(DelegationRef delegationRef) {
        restClient.approveConsumerDelegation(delegationRef.id()).withPolling(PollingStrategy.UNTIL_SUCCESS);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

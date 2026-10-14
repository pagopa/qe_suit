package it.pagopa.interop.bff.delegation.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ConsumerDelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Delegation;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffDelegationRestClient extends RestClient {

    private final ConsumerDelegationsApi consumerDelegationsApi;

    public BffDelegationRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.consumerDelegationsApi = apiClient.consumerDelegations();
    }

    public TestChain<Delegation> createConsumerDelegation(
            @Nonnull UUID delegateId,
            @Nonnull UUID eServiceId
    ) {
        DelegationSeed seed = new DelegationSeed();
        seed.setDelegateId(delegateId);
        seed.setEserviceId(eServiceId);
        return execute(
                () -> consumerDelegationsApi.createConsumerDelegation()
                        .body(seed)
                        .execute(Function.identity()),
                Delegation.class
        );
    }

    public TestChain<Void> approveConsumerDelegation(@Nonnull UUID delegationId) {
        return execute(
                () -> consumerDelegationsApi.approveConsumerDelegation()
                        .delegationIdPath(delegationId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }
}

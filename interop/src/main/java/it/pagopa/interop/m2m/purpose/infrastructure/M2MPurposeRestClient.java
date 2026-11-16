package it.pagopa.interop.m2m.purpose.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.DelegationRef;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.PurposeVersionSeed;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MPurposeRestClient extends RestClient {

    private final PurposesApi purposesApi;

    public M2MPurposeRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.purposesApi = apiClient.purposes();
    }

    public TestChain<Purpose> getPurpose(@Nonnull UUID purposeId) {
        return execute(
                () -> purposesApi.getPurpose()
                        .purposeIdPath(purposeId)
                        .execute(Function.identity()),
                Purpose.class
        );
    }

    public TestChain<Purpose> createPurpose(@Nonnull PurposeSeed payload) {
        return execute(
                () -> purposesApi.createPurpose()
                        .body(payload)
                        .execute(Function.identity()),
                Purpose.class
        );
    }

    public TestChain<Purpose> approvePurpose(@Nonnull UUID purposeId, DelegationRef delegationRef) {
        return execute(
                () -> purposesApi.approvePurpose()
                        .purposeIdPath(purposeId)
                        .body(delegationRef)
                        .execute(Function.identity()),
                Purpose.class
        );
    }

    public TestChain<PurposeVersion> createPurposeVersion(@Nonnull UUID purposeId, @Nonnull PurposeVersionSeed payload) {
        return execute(
                () -> purposesApi.createPurposeVersion()
                        .purposeIdPath(purposeId)
                        .body(payload)
                        .execute(Function.identity()),
                PurposeVersion.class
        );
    }
}

package it.pagopa.interop.bff.purpose.infratructure;

import it.pagopa.interop.common.infrastructure.template.RestClient;
import it.pagopa.interop.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffPurposeRestClient extends RestClient {

    private final PurposesApi purposesApi;

    public BffPurposeRestClient(ApiClient apiClient) {
        this.purposesApi = apiClient.purposes();
    }

    public TestChain<Purpose> getPurpose(@Nonnull UUID purposeId) {
        return execute(
                () -> purposesApi.getPurpose().purposeIdPath(purposeId).execute(Function.identity()),
                Purpose.class
        );
    }

    public TestChain<CreatedResource> createPurpose(@Nonnull PurposeSeed payload) {
        return execute(
                () -> purposesApi.createPurpose().body(payload).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<PurposeVersionResource> activatePurposeVersion(@Nonnull UUID purposeId, @Nonnull UUID versionId, DelegationRef delegationRef) {
        return execute(
                () -> purposesApi.activatePurposeVersion()
                        .purposeIdPath(purposeId)
                        .versionIdPath(versionId)
                        .body(delegationRef)
                        .execute(Function.identity()),
                PurposeVersionResource.class
        );
    }

    public TestChain<PurposeVersionResource> suspendPurposeVersion(@Nonnull UUID purposeId, @Nonnull UUID versionId, DelegationRef delegationRef) {
        return execute(
                () -> purposesApi.suspendPurposeVersion()
                        .purposeIdPath(purposeId)
                        .versionIdPath(versionId)
                        .body(delegationRef)
                        .execute(Function.identity()),
                PurposeVersionResource.class
        );
    }
}

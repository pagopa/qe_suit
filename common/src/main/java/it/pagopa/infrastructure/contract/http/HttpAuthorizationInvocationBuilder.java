package it.pagopa.infrastructure.contract.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

final class HttpAuthorizationInvocationBuilder implements HttpAuthorizationStages.ApiCallsStage {
    private final OpenApiOperationAdapter operationAdapter;
    private final List<HttpContractIdentity> identities;

    HttpAuthorizationInvocationBuilder(
            OpenApiOperationAdapter operationAdapter,
            List<HttpContractIdentity> identities
    ) {
        this.operationAdapter = operationAdapter;
        this.identities = copyIdentities(identities);
    }

    @Override
    public HttpAuthorizationStages.ExpectationsStage apiCalls(List<? extends Supplier<?>> operations) {
        return new HttpAuthorizationExpectationsStage(operationAdapter, identities, copyOperations(operations));
    }

    @Override
    public HttpAuthorizationStages.ExpectationsStage apiCalls(Supplier<?>... operations) {
        if (operations == null) {
            throw new ContractHttpException("operation suppliers must not be null");
        }
        return apiCalls(Arrays.asList(operations));
    }

    private static List<HttpContractIdentity> copyIdentities(List<HttpContractIdentity> identities) {
        if (identities == null) {
            throw new ContractHttpException("identities must not be null");
        }
        List<HttpContractIdentity> copy = new ArrayList<>(identities);
        if (copy.isEmpty()) {
            throw new ContractHttpException("identities must not be empty");
        }
        for (int index = 0; index < copy.size(); index++) {
            HttpContractIdentity identity = copy.get(index);
            if (identity == null) {
                throw new ContractHttpException("identity #" + (index + 1) + " must not be null");
            }
            if (identity.authentication() == null) {
                throw new ContractHttpException("identity #" + (index + 1) + " authentication must not be null");
            }
        }
        return List.copyOf(copy);
    }

    private static List<Supplier<?>> copyOperations(List<? extends Supplier<?>> operations) {
        if (operations == null) {
            throw new ContractHttpException("operation suppliers must not be null");
        }
        List<Supplier<?>> copy = new ArrayList<>(operations);
        if (copy.isEmpty()) {
            throw new ContractHttpException("operation suppliers must not be empty");
        }
        for (int index = 0; index < copy.size(); index++) {
            if (copy.get(index) == null) {
                throw new ContractHttpException("operation supplier #" + (index + 1) + " must not be null");
            }
        }
        return List.copyOf(copy);
    }
}

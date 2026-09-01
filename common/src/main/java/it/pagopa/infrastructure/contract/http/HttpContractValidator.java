package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;

import java.util.Objects;
import java.util.function.Supplier;

public final class HttpContractValidator {
    private final ObjectMapper objectMapper;
    private final FuzzEngine fuzzEngine;
    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final ContractCasePlanner casePlanner;
    private final OpenApiOperationAdapter operationAdapter;

    public HttpContractValidator(ObjectMapper objectMapper, FuzzEngine fuzzEngine, ObjectGraphDecomposer objectGraphDecomposer, HttpContractPolicy policy) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.fuzzEngine = Objects.requireNonNull(fuzzEngine, "fuzzEngine must not be null");
        this.objectGraphDecomposer = Objects.requireNonNull(objectGraphDecomposer, "objectGraphDecomposer must not be null");
        Objects.requireNonNull(policy, "policy must not be null");
        this.casePlanner = new ContractCasePlanner(objectMapper, new MockitoObjectGraphQueryResolver(), policy);
        this.operationAdapter = new OpenApiOperationAdapter(objectMapper);
    }

    public HttpContractStages.ApiCallStage apiCall(Supplier<? extends Oper> operationSupplier) {
        if (operationSupplier == null) throw new ContractHttpException("operation supplier must not be null");
        return new HttpContractInvocationBuilder(
                objectMapper,
                fuzzEngine,
                objectGraphDecomposer,
                casePlanner,
                operationAdapter,
                operationSupplier
        );
    }
}

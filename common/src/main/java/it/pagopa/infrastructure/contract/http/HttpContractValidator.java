package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class HttpContractValidator {
    private final ObjectMapper objectMapper;
    private final FuzzEngine payloadFuzzEngine;
    private final FuzzEngine pathParamsFuzzEngine;
    private final FuzzEngine queryParamsFuzzEngine;
    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final ContractCasePlanner casePlanner;
    private final OpenApiOperationAdapter operationAdapter;

    /**
     * Keeps the query parameter scope on the path parameter engine, so existing configurations
     * behave exactly as before.
     */
    public HttpContractValidator(
            ObjectMapper objectMapper,
            FuzzEngine payloadFuzzEngine,
            FuzzEngine pathParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            HttpContractPolicy policy
    ) {
        this(objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, pathParamsFuzzEngine, objectGraphDecomposer, policy);
    }

    public HttpContractValidator(
            ObjectMapper objectMapper,
            FuzzEngine payloadFuzzEngine,
            FuzzEngine pathParamsFuzzEngine,
            FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            HttpContractPolicy policy
    ) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.payloadFuzzEngine = Objects.requireNonNull(payloadFuzzEngine, "payloadFuzzEngine must not be null");
        this.pathParamsFuzzEngine = Objects.requireNonNull(pathParamsFuzzEngine, "pathParamsFuzzEngine must not be null");
        this.queryParamsFuzzEngine = Objects.requireNonNull(queryParamsFuzzEngine, "queryParamsFuzzEngine must not be null");
        this.objectGraphDecomposer = Objects.requireNonNull(
                objectGraphDecomposer,
                "objectGraphDecomposer must not be null"
        );

        Objects.requireNonNull(policy, "policy must not be null");

        this.casePlanner = new ContractCasePlanner(
                objectMapper,
                new MockitoObjectGraphQueryResolver(),
                policy
        );
        this.operationAdapter = new OpenApiOperationAdapter(objectMapper);
    }

    public HttpContractStages.ApiCallStage apiCall(
            HttpContractAuthentication authentication,
            Supplier<?> operationSupplier
    ) {
        Objects.requireNonNull(authentication, "authentication must not be null");
        if (operationSupplier == null) {
            throw new ContractHttpException("operation supplier must not be null");
        }

        return new HttpContractInvocationBuilder(
                objectMapper,
                payloadFuzzEngine,
                pathParamsFuzzEngine,
                queryParamsFuzzEngine,
                objectGraphDecomposer,
                casePlanner,
                operationAdapter,
                authentication,
                operationSupplier
        );
    }

    public HttpContractStages.ApiCallStage apiCall(Supplier<?> operationSupplier) {
        return apiCall(HttpContractAuthentication.noOp(), operationSupplier);
    }

    /**
     * Genera un test per ogni combinazione utenza x API, verificando lo status HTTP atteso
     * (default 403) senza mutazioni della richiesta.
     */
    public HttpAuthorizationStages.ApiCallsStage as(List<HttpContractIdentity> identities) {
        return new HttpAuthorizationInvocationBuilder(operationAdapter, identities);
    }

    public HttpAuthorizationStages.ApiCallsStage as(Stream<HttpContractIdentity> identities) {
        if (identities == null) {
            throw new ContractHttpException("identities must not be null");
        }
        return as(identities.toList());
    }
}

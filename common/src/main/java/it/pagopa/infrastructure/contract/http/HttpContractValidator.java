package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

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
     * Modalita' autorizzazione: per ogni combinazione identita' x API genera un test dinamico che
     * autentica l'utenza, costruisce una nuova operazione tramite il supplier, esegue la richiesta
     * e verifica lo status esatto atteso ({@code 403} se non diversamente indicato).
     * <p>
     * Percorso distinto da quello di fuzzing: non usa i {@code FuzzEngine}, il
     * {@code ContractCasePlanner} ne' la {@link HttpContractPolicy}, e non genera mutazioni.
     */
    public HttpContractStages.AuthorizationApiCallsStage authorization(
            List<HttpContractIdentity> identities
    ) {
        return new HttpAuthorizationInvocationBuilder(identities, operationAdapter);
    }
}
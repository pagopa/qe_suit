package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.logging.TestMdcKeys;
import org.slf4j.MDC;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

final class HttpContractRuntimeCaseExecutor {
    private final ObjectMapper objectMapper;
    private final FuzzEngine payloadFuzzEngine;
    private final FuzzEngine pathParamsFuzzEngine;
    private final FuzzEngine queryParamsFuzzEngine;
    private final OpenApiOperationAdapter operationAdapter;
    private final HttpContractAuthentication authentication;

    HttpContractRuntimeCaseExecutor(
            ObjectMapper objectMapper,
            FuzzEngine payloadFuzzEngine,
            FuzzEngine pathParamsFuzzEngine,
            FuzzEngine queryParamsFuzzEngine,
            OpenApiOperationAdapter operationAdapter,
            HttpContractAuthentication authentication
    ) {
        this.objectMapper = objectMapper;
        this.payloadFuzzEngine = payloadFuzzEngine;
        this.pathParamsFuzzEngine = pathParamsFuzzEngine;
        this.queryParamsFuzzEngine = queryParamsFuzzEngine;
        this.operationAdapter = operationAdapter;
        this.authentication = Objects.requireNonNull(authentication, "authentication must not be null");
    }

    void execute(
            String testName,
            GeneratedContractCase testCase,
            ScopeState<?> payloadState,
            ScopeState<?> pathState,
            ScopeState<?> queryState,
            Supplier<?> operationSupplier
    ) {
        String previousScenarioName = MDC.get(TestMdcKeys.SCENARIO_NAME);
        MDC.put(TestMdcKeys.SCENARIO_NAME, testName);
        try {
            RuntimeScope payloadRuntime = materializeRuntimeScope(payloadState, RequestScope.PAYLOAD, testCase);
            RuntimeScope pathRuntime = materializeRuntimeScope(pathState, RequestScope.PATH_PARAMS, testCase);
            RuntimeScope queryRuntime = materializeRuntimeScope(queryState, RequestScope.QUERY_PARAMS, testCase);
            authentication.authenticate();
            Object operation = materializeOperation(operationSupplier, testCase);
            HttpContractRequest request = buildRuntimeRequest(testCase, payloadRuntime, pathRuntime, queryRuntime);

            Response response = operationAdapter.execute(operation, request);
            try {
                testCase.expectation().accept(response);
            } catch (AssertionError exception) {
                throw HttpContractFailureDiagnostics.enrich(exception, testCase, request, response, objectMapper);
            }
        } finally {
            // Ripristina il contesto del @TestFactory padre invece di azzerarlo:
            // i casi dinamici successivi condividono lo stesso thread.
            if (previousScenarioName != null) {
                MDC.put(TestMdcKeys.SCENARIO_NAME, previousScenarioName);
            } else {
                MDC.remove(TestMdcKeys.SCENARIO_NAME);
            }
        }
    }

    Object materializeSource(
            Supplier<?> supplier,
            RequestScope scope,
            String phase,
            GeneratedContractCase testCase
    ) {
        try {
            Object source = supplier.get();
            if (source == null) {
                throw new ContractHttpException(
                        scopeLabel(scope) + " supplier returned null during " + phase + suffix(testCase)
                );
            }
            return source;
        } catch (ContractHttpException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ContractHttpException(
                    scopeLabel(scope) + " supplier failed during " + phase + suffix(testCase),
                    exception
            );
        }
    }

    private HttpContractRequest buildRuntimeRequest(
            GeneratedContractCase testCase,
            RuntimeScope payloadRuntime,
            RuntimeScope pathRuntime,
            RuntimeScope queryRuntime
    ) {
        JsonNode payloadBaseline = payloadRuntime == null ? null : payloadRuntime.baseline();
        JsonNode pathBaseline = pathRuntime == null ? null : pathRuntime.baseline();
        JsonNode queryBaseline = queryRuntime == null ? null : queryRuntime.baseline();
        FuzzCase runtimeCase = resolveRuntimeCase(testCase, payloadRuntime, pathRuntime, queryRuntime);
        JsonNode mutated = runtimeCase.result();

        return switch (testCase.scope()) {
            case PAYLOAD -> new HttpContractRequest(mutated, mutated != null, pathBaseline, queryBaseline);
            case PATH_PARAMS -> new HttpContractRequest(payloadBaseline, payloadRuntime != null, mutated, queryBaseline);
            case QUERY_PARAMS -> new HttpContractRequest(payloadBaseline, payloadRuntime != null, pathBaseline, mutated);
        };
    }

    private FuzzCase resolveRuntimeCase(
            GeneratedContractCase testCase,
            RuntimeScope payloadRuntime,
            RuntimeScope pathRuntime,
            RuntimeScope queryRuntime
    ) {
        RuntimeScope mutatedScope = switch (testCase.scope()) {
            case PAYLOAD -> payloadRuntime;
            case PATH_PARAMS -> pathRuntime;
            case QUERY_PARAMS -> queryRuntime;
        };
        if (mutatedScope == null) {
            throw new ContractHttpException(
                    "Missing configured scope for case " + formatDescriptor(testCase.descriptor())
            );
        }

        List<FuzzCase> matches = fuzzEngine(testCase.scope()).generate(mutatedScope.source()).stream()
                .filter(candidate -> candidate.target().equals(testCase.target()))
                .filter(candidate -> candidate.mutation().scenario() == testCase.mutation().scenario())
                .toList();

        if (matches.isEmpty()) {
            throw new ContractHttpException(
                    "Cannot rebuild planned case on fresh baseline: " + formatDescriptor(testCase.descriptor())
            );
        }
        if (matches.size() > 1) {
            throw new ContractHttpException(
                    "Non-deterministic runtime case rebuild (multiple matches): "
                            + formatDescriptor(testCase.descriptor())
            );
        }
        return matches.get(0);
    }

    private RuntimeScope materializeRuntimeScope(
            ScopeState<?> state,
            RequestScope scope,
            GeneratedContractCase testCase
    ) {
        if (state == null) return null;

        Object source = materializeSource(state.sourceSupplier(), scope, "execution", testCase);
        return new RuntimeScope(source, objectMapper.valueToTree(source));
    }

    private Object materializeOperation(
            Supplier<?> operationSupplier,
            GeneratedContractCase testCase
    ) {
        try {
            Object operation = operationSupplier.get();
            if (operation == null) {
                throw new ContractHttpException(
                        "operation supplier returned null for " + formatDescriptor(testCase.descriptor())
                );
            }
            return operation;
        } catch (ContractHttpException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ContractHttpException(
                    "operation supplier failed for " + formatDescriptor(testCase.descriptor()),
                    exception
            );
        }
    }

    private String suffix(GeneratedContractCase testCase) {
        return testCase == null ? "" : " for " + formatDescriptor(testCase.descriptor());
    }

    private String scopeLabel(RequestScope scope) {
        return switch (scope) {
            case PAYLOAD -> "payload";
            case PATH_PARAMS -> "pathParams";
            case QUERY_PARAMS -> "queryParams";
        };
    }

    private String formatDescriptor(ContractCaseDescriptor descriptor) {
        String target = descriptor.target().isRoot() ? "<root>" : descriptor.target().toString();
        return descriptor.scope() + " " + descriptor.scenario() + " @ " + target;
    }

    private FuzzEngine fuzzEngine(RequestScope scope) {
        return switch (scope) {
            case PAYLOAD -> payloadFuzzEngine;
            case PATH_PARAMS -> pathParamsFuzzEngine;
            case QUERY_PARAMS -> queryParamsFuzzEngine;
        };
    }

    private record RuntimeScope(Object source, JsonNode baseline) {
    }
}
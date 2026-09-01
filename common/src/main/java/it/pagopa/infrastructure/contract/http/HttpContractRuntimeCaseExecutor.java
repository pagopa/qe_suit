package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import org.slf4j.MDC;

import java.util.List;
import java.util.function.Supplier;

final class HttpContractRuntimeCaseExecutor {
    private final ObjectMapper objectMapper;
    private final FuzzEngine fuzzEngine;
    private final OpenApiOperationAdapter operationAdapter;

    HttpContractRuntimeCaseExecutor(
            ObjectMapper objectMapper,
            FuzzEngine fuzzEngine,
            OpenApiOperationAdapter operationAdapter
    ) {
        this.objectMapper = objectMapper;
        this.fuzzEngine = fuzzEngine;
        this.operationAdapter = operationAdapter;
    }

    void execute(
            String testName,
            GeneratedContractCase testCase,
            ScopeState<?> payloadState,
            ScopeState<?> pathState,
            Supplier<?> operationSupplier
    ) {
        MDC.put("scenario", testName);
        try {
            RuntimeScope payloadRuntime = materializeRuntimeScope(payloadState, RequestScope.PAYLOAD, testCase);
            RuntimeScope pathRuntime = materializeRuntimeScope(pathState, RequestScope.PATH_PARAMS, testCase);
            Object operation = materializeOperation(operationSupplier, testCase);
            HttpContractRequest request = buildRuntimeRequest(testCase, payloadRuntime, pathRuntime);

            Response response = operationAdapter.execute(operation, request);
            try {
                testCase.expectation().accept(response);
            } catch (AssertionError exception) {
                throw HttpContractFailureDiagnostics.enrich(exception, testCase, request, response, objectMapper);
            }
        } finally {
            MDC.remove("scenario");
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
            RuntimeScope pathRuntime
    ) {
        JsonNode payloadBaseline = payloadRuntime == null ? null : payloadRuntime.baseline();
        JsonNode pathBaseline = pathRuntime == null ? null : pathRuntime.baseline();
        FuzzCase runtimeCase = resolveRuntimeCase(testCase, payloadRuntime, pathRuntime);
        JsonNode mutated = runtimeCase.result();

        if (testCase.scope() == RequestScope.PAYLOAD) {
            return new HttpContractRequest(mutated, mutated != null, pathBaseline);
        }
        return new HttpContractRequest(payloadBaseline, payloadRuntime != null, mutated);
    }

    private FuzzCase resolveRuntimeCase(
            GeneratedContractCase testCase,
            RuntimeScope payloadRuntime,
            RuntimeScope pathRuntime
    ) {
        RuntimeScope mutatedScope = testCase.scope() == RequestScope.PAYLOAD ? payloadRuntime : pathRuntime;
        if (mutatedScope == null) {
            throw new ContractHttpException(
                    "Missing configured scope for case " + formatDescriptor(testCase.descriptor())
            );
        }

        List<FuzzCase> matches = fuzzEngine.generate(mutatedScope.source()).stream()
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
        return scope == RequestScope.PAYLOAD ? "payload" : "pathParams";
    }

    private String formatDescriptor(ContractCaseDescriptor descriptor) {
        String target = descriptor.target().isRoot() ? "<root>" : descriptor.target().toString();
        return descriptor.scope() + " " + descriptor.scenario() + " @ " + target;
    }

    private record RuntimeScope(Object source, JsonNode baseline) {
    }
}
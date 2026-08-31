package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;
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
            Supplier<? extends Oper> operationSupplier
    ) {
        MDC.put("scenario", testName);
        try {
            RuntimeScope payloadRuntime = materializeRuntimeScope(payloadState, RequestScope.PAYLOAD, testCase);
            RuntimeScope pathRuntime = materializeRuntimeScope(pathState, RequestScope.PATH_PARAMS, testCase);
            Oper operation = materializeOperation(operationSupplier, testCase);
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

    Object materializeSource(Supplier<?> supplier, RequestScope scope, String phase, GeneratedContractCase testCase) {
        Object source;
        try {
            source = supplier.get();
        } catch (RuntimeException exception) {
            throw new ContractHttpException(scopeLabel(scope) + " supplier failed during " + phase + suffix(testCase), exception);
        }
        if (source == null) {
            throw new ContractHttpException(scopeLabel(scope) + " supplier returned null during " + phase + suffix(testCase));
        }
        return source;
    }

    private HttpContractRequest buildRuntimeRequest(GeneratedContractCase testCase, RuntimeScope payloadRuntime, RuntimeScope pathRuntime) {
        JsonNode payloadBaseline = payloadRuntime == null ? null : payloadRuntime.baseline();
        JsonNode pathBaseline = pathRuntime == null ? null : pathRuntime.baseline();
        FuzzCase runtimeCase = resolveRuntimeCase(testCase, payloadRuntime, pathRuntime);
        JsonNode mutated = runtimeCase.result();
        if (testCase.scope() == RequestScope.PAYLOAD) {
            return new HttpContractRequest(mutated, mutated != null, pathBaseline);
        }
        return new HttpContractRequest(payloadBaseline, payloadRuntime != null, mutated);
    }

    private FuzzCase resolveRuntimeCase(GeneratedContractCase testCase, RuntimeScope payloadRuntime, RuntimeScope pathRuntime) {
        RuntimeScope mutatedScope = testCase.scope() == RequestScope.PAYLOAD ? payloadRuntime : pathRuntime;
        if (mutatedScope == null) {
            throw new ContractHttpException("Missing configured scope for case " + formatDescriptor(testCase.descriptor()));
        }
        List<FuzzCase> freshCases = fuzzEngine.generate(mutatedScope.source());
        List<FuzzCase> matches = freshCases.stream()
                .filter(candidate -> candidate.target().equals(testCase.target()))
                .filter(candidate -> candidate.mutation().scenario() == testCase.mutation().scenario())
                .toList();
        if (matches.isEmpty()) {
            throw new ContractHttpException("Cannot rebuild planned case on fresh baseline: " + formatDescriptor(testCase.descriptor()));
        }
        if (matches.size() > 1) {
            throw new ContractHttpException("Non-deterministic runtime case rebuild (multiple matches): " + formatDescriptor(testCase.descriptor()));
        }
        return matches.get(0);
    }

    private RuntimeScope materializeRuntimeScope(ScopeState<?> state, RequestScope scope, GeneratedContractCase testCase) {
        if (state == null) return null;
        Object source = materializeSource(state.sourceSupplier(), scope, "execution", testCase);
        return new RuntimeScope(source, objectMapper.valueToTree(source));
    }

    private Oper materializeOperation(Supplier<? extends Oper> operationSupplier, GeneratedContractCase testCase) {
        Oper operation;
        try {
            operation = operationSupplier.get();
        } catch (RuntimeException exception) {
            throw new ContractHttpException("operation supplier failed for " + formatDescriptor(testCase.descriptor()), exception);
        }
        if (operation == null) {
            throw new ContractHttpException("operation supplier returned null for " + formatDescriptor(testCase.descriptor()));
        }
        return operation;
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

    private record RuntimeScope(
            Object source,
            JsonNode baseline
    ) {
    }
}

package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class HttpContractInvocationBuilder implements HttpContractStages.ApiCallStage {
    private final FuzzEngine fuzzEngine;
    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final ContractCasePlanner casePlanner;
    private final Supplier<? extends Oper> operationSupplier;
    private final HttpContractRuntimeCaseExecutor runtimeCaseExecutor;
    private ScopeState<?> payloadState;
    private ScopeState<?> pathState;

    HttpContractInvocationBuilder(
            ObjectMapper objectMapper,
            FuzzEngine fuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            ContractCasePlanner casePlanner,
            OpenApiOperationAdapter operationAdapter,
            Supplier<? extends Oper> operationSupplier
    ) {
        this.fuzzEngine = fuzzEngine;
        this.objectGraphDecomposer = objectGraphDecomposer;
        this.casePlanner = casePlanner;
        this.operationSupplier = operationSupplier;
        this.runtimeCaseExecutor = new HttpContractRuntimeCaseExecutor(objectMapper, fuzzEngine, operationAdapter);
    }

    @Override
    public <T> HttpContractStages.PayloadStage<T> payload(Supplier<T> payloadSupplier) {
        this.payloadState = createScope(payloadSupplier, "payload");
        return new HttpContractPayloadStage<>(this, payloadState.overrides());
    }

    @Override
    public <T> HttpContractStages.PathParamsStage<T> pathParams(Supplier<T> pathParamsSupplier) {
        this.pathState = createScope(pathParamsSupplier, "pathParams");
        return new HttpContractPathParamsStage<>(this, pathState.overrides());
    }

    @Override
    public Stream<DynamicTest> tests() {
        List<GeneratedContractCase> cases = casePlanner.planCases(
                planScope(payloadState, RequestScope.PAYLOAD),
                planScope(pathState, RequestScope.PATH_PARAMS)
        );
        return cases.stream().map(this::toDynamicTest);
    }

    private DynamicTest toDynamicTest(GeneratedContractCase testCase) {
        String target = testCase.target().isRoot() ? "<root>" : testCase.target().toString();
        String name = "[" + (testCase.scope() == RequestScope.PAYLOAD ? "payload" : "pathParams")
                + "] " + testCase.mutation().scenario() + " @ " + target;
        return dynamicTest(name, () -> runtimeCaseExecutor.execute(name, testCase, payloadState, pathState, operationSupplier));
    }

    @SuppressWarnings("unchecked")
    private ScopePlanState<?> planScope(ScopeState<?> state, RequestScope scope) {
        if (state == null) return null;
        Object source = runtimeCaseExecutor.materializeSource(state.sourceSupplier(), scope, "discovery", null);
        ObjectGraph graph = objectGraphDecomposer.decompose(source);
        return new ScopePlanState<>(
                source,
                (Class<Object>) source.getClass(),
                graph,
                fuzzEngine.generate(source),
                state.overrides()
        );
    }

    private <T> ScopeState<T> createScope(Supplier<T> supplier, String scopeName) {
        if (supplier == null) throw new ContractHttpException(scopeName + " supplier must not be null");
        return new ScopeState<>(supplier, new ScopeOverrides());
    }
}

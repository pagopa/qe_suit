package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;
import org.junit.jupiter.api.DynamicTest;
import org.slf4j.MDC;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public final class HttpContract {
    private final ObjectMapper objectMapper;
    private final FuzzEngine fuzzEngine;
    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final ContractCasePlanner casePlanner;
    private final OpenApiOperationAdapter operationAdapter;

    public HttpContract(ObjectMapper objectMapper, FuzzEngine fuzzEngine, ObjectGraphDecomposer objectGraphDecomposer, HttpContractPolicy policy) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.fuzzEngine = Objects.requireNonNull(fuzzEngine, "fuzzEngine must not be null");
        this.objectGraphDecomposer = Objects.requireNonNull(objectGraphDecomposer, "objectGraphDecomposer must not be null");
        Objects.requireNonNull(policy, "policy must not be null");
        this.casePlanner = new ContractCasePlanner(objectMapper, new MockitoObjectGraphQueryResolver(), policy);
        this.operationAdapter = new OpenApiOperationAdapter(objectMapper);
    }

    public HttpContractStages.ApiCallStage apiCall(Supplier<? extends Oper> operationSupplier) {
        if (operationSupplier == null) throw new ContractHttpException("operation supplier must not be null");
        return new InvocationBuilder(operationSupplier);
    }

    private final class InvocationBuilder implements HttpContractStages.ApiCallStage {
        private final Supplier<? extends Oper> operationSupplier;
        private ScopeState<?> payloadState;
        private ScopeState<?> pathState;

        private InvocationBuilder(Supplier<? extends Oper> operationSupplier) {
            this.operationSupplier = operationSupplier;
        }

        @Override
        public <T> HttpContractStages.PayloadStage<T> payload(Supplier<T> payloadSupplier) {
            this.payloadState = createScope(payloadSupplier, "payload");
            return new PayloadStageImpl<>(this, this.payloadState.overrides());
        }

        @Override
        public <T> HttpContractStages.PathParamsStage<T> pathParams(Supplier<T> pathParamsSupplier) {
            this.pathState = createScope(pathParamsSupplier, "pathParams");
            return new PathParamsStageImpl<>(this, this.pathState.overrides());
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
            String name =
                    "[" + (testCase.scope() == RequestScope.PAYLOAD ? "payload" : "pathParams")
                            + "] "
                            + testCase.mutation().scenario()
                            + " @ "
                            + target;

            return dynamicTest(name, () -> {
                MDC.put("scenario", name);
                try {
                    RuntimeScope payloadRuntime = materializeRuntimeScope(payloadState, RequestScope.PAYLOAD, testCase);
                    RuntimeScope pathRuntime = materializeRuntimeScope(pathState, RequestScope.PATH_PARAMS, testCase);
                    Oper operation = materializeOperation(testCase);
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
            });
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
                throw new ContractHttpException(
                        "Cannot rebuild planned case on fresh baseline: " + formatDescriptor(testCase.descriptor())
                );
            }
            if (matches.size() > 1) {
                throw new ContractHttpException(
                        "Non-deterministic runtime case rebuild (multiple matches): " + formatDescriptor(testCase.descriptor())
                );
            }
            return matches.get(0);
        }

        private Oper materializeOperation(GeneratedContractCase testCase) {
            Oper operation;
            try {
                operation = operationSupplier.get();
            } catch (RuntimeException exception) {
                throw new ContractHttpException(
                        "operation supplier failed for " + formatDescriptor(testCase.descriptor()),
                        exception
                );
            }
            if (operation == null) {
                throw new ContractHttpException(
                        "operation supplier returned null for " + formatDescriptor(testCase.descriptor())
                );
            }
            return operation;
        }

        private RuntimeScope materializeRuntimeScope(ScopeState<?> state, RequestScope scope, GeneratedContractCase testCase) {
            if (state == null) return null;
            Object source = materializeSource(state.sourceSupplier(), scope, "execution", testCase);
            return new RuntimeScope(source, objectMapper.valueToTree(source));
        }

        @SuppressWarnings("unchecked")
        private ScopePlanState<?> planScope(ScopeState<?> state, RequestScope scope) {
            if (state == null) return null;
            Object source = materializeSource(state.sourceSupplier(), scope, "discovery", null);
            ObjectGraph graph = objectGraphDecomposer.decompose(source);
            return new ScopePlanState<>(
                    source,
                    (Class<Object>) source.getClass(),
                    graph,
                    fuzzEngine.generate(source),
                    state.overrides()
            );
        }

        private Object materializeSource(Supplier<?> supplier, RequestScope scope, String phase, GeneratedContractCase testCase) {
            Object source;
            try {
                source = supplier.get();
            } catch (RuntimeException exception) {
                throw new ContractHttpException(
                        scopeLabel(scope) + " supplier failed during " + phase + suffix(testCase),
                        exception
                );
            }
            if (source == null) {
                throw new ContractHttpException(scopeLabel(scope) + " supplier returned null during " + phase + suffix(testCase));
            }
            return source;
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

        private <T> ScopeState<T> createScope(Supplier<T> supplier, String scopeName) {
            if (supplier == null) throw new ContractHttpException(scopeName + " supplier must not be null");
            return new ScopeState<>(supplier, new ScopeOverrides());
        }
    }

    private static final class PayloadStageImpl<T> implements HttpContractStages.PayloadStage<T> {
        private final InvocationBuilder builder;
        private final ScopeOverrides overrides;

        private PayloadStageImpl(InvocationBuilder builder, ScopeOverrides overrides) {
            this.builder = builder;
            this.overrides = overrides;
        }

        @Override
        public PayloadStageImpl<T> scenario(FuzzScenario scenario, Consumer<Response> expectation) {
            return scenario(List.of(scenario), expectation);
        }

        @Override
        public PayloadStageImpl<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation) {
            overrides.addScenario(scenarios, expectation);
            return this;
        }

        @Override
        public PayloadStageImpl<T> targets(FuzzScenario scenario, Consumer<Response> expectation, TargetExpression<T>... targets) {
            return targets(List.of(scenario), expectation, targets);
        }

        @Override
        public PayloadStageImpl<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, TargetExpression<T>... targets) {
            overrides.addTargets(scenarios, expectation, targets);
            return this;
        }

        @Override
        public <P> HttpContractStages.PathParamsStage<P> pathParams(Supplier<P> pathParamsSupplier) {
            return builder.pathParams(pathParamsSupplier);
        }

        @Override
        public Stream<DynamicTest> tests() {
            return builder.tests();
        }
    }

    private static final class PathParamsStageImpl<T> implements HttpContractStages.PathParamsStage<T> {
        private final InvocationBuilder builder;
        private final ScopeOverrides overrides;

        private PathParamsStageImpl(InvocationBuilder builder, ScopeOverrides overrides) {
            this.builder = builder;
            this.overrides = overrides;
        }

        @Override
        public PathParamsStageImpl<T> scenario(FuzzScenario scenario, Consumer<Response> expectation) {
            return scenario(List.of(scenario), expectation);
        }

        @Override
        public PathParamsStageImpl<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation) {
            overrides.addScenario(scenarios, expectation);
            return this;
        }

        @Override
        public PathParamsStageImpl<T> targets(FuzzScenario scenario, Consumer<Response> expectation, TargetExpression<T>... targets) {
            return targets(List.of(scenario), expectation, targets);
        }

        @Override
        public PathParamsStageImpl<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, TargetExpression<T>... targets) {
            overrides.addTargets(scenarios, expectation, targets);
            return this;
        }

        @Override
        public <P> HttpContractStages.PayloadStage<P> payload(Supplier<P> payloadSupplier) {
            return builder.payload(payloadSupplier);
        }

        @Override
        public Stream<DynamicTest> tests() {
            return builder.tests();
        }
    }

    private record RuntimeScope(
            Object source,
            JsonNode baseline
    ) {
    }
}

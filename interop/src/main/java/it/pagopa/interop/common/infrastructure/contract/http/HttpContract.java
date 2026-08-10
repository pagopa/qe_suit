package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DynamicTest;
import org.slf4j.MDC;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@Slf4j
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
        public <T> HttpContractStages.PayloadStage<T> payload(T payload) {
            this.payloadState = createScope(payload);
            return new PayloadStageImpl<>(this, this.payloadState.overrides());
        }

        @Override
        public <T> HttpContractStages.PathParamsStage<T> pathParams(T pathParams) {
            this.pathState = createScope(pathParams);
            return new PathParamsStageImpl<>(this, this.pathState.overrides());
        }

        @Override
        public Stream<DynamicTest> tests() {
            List<GeneratedContractCase> cases = casePlanner.planCases(payloadState, pathState);
            return cases.stream().map(this::toDynamicTest);
        }

        private DynamicTest toDynamicTest(GeneratedContractCase testCase) {
            String target = testCase.target().isRoot() ? "<root>" : testCase.target().toString();

            String name =
                    "[" + (testCase.scope() == RequestScope.PAYLOAD
                            ? "payload"
                            : "pathParams")
                            + "] "
                            + testCase.fuzzCase().mutation().scenario()
                            + " @ "
                            + target;

            return dynamicTest(name, () -> {
                MDC.put("scenario", name);

                try {
                    Oper operation = operationSupplier.get();

                    if (operation == null)
                        throw new ContractHttpException(
                                "operation supplier returned null"
                        );

                    Response response = operationAdapter.execute(operation, testCase.request());

                    try {
                        testCase.expectation().accept(response);
                    } catch (AssertionError exception) {
                        throw HttpContractFailureDiagnostics.enrich(exception, testCase, response, objectMapper);
                    }

                } finally {
                    MDC.remove("scenario");
                }
            });
        }

        @SuppressWarnings("unchecked")
        private <T> ScopeState<T> createScope(T source) {
            if (source == null) throw new ContractHttpException("scope source must not be null");
            ObjectGraph graph = objectGraphDecomposer.decompose(source);
            return new ScopeState<>(source, (Class<T>) source.getClass(), graph, fuzzEngine.generate(source), new ScopeOverrides());
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
        public <P> HttpContractStages.PathParamsStage<P> pathParams(P pathParams) {
            return builder.pathParams(pathParams);
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
        public <P> HttpContractStages.PayloadStage<P> payload(P payload) {
            return builder.payload(payload);
        }

        @Override
        public Stream<DynamicTest> tests() {
            return builder.tests();
        }
    }
}

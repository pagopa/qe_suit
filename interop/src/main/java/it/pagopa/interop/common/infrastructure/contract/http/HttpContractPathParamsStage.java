package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class HttpContractPathParamsStage<T> implements HttpContractStages.PathParamsStage<T> {
    private final HttpContractInvocationBuilder builder;
    private final ScopeOverrides overrides;

    HttpContractPathParamsStage(HttpContractInvocationBuilder builder, ScopeOverrides overrides) {
        this.builder = builder;
        this.overrides = overrides;
    }

    @Override
    public HttpContractPathParamsStage<T> scenario(FuzzScenario scenario, Consumer<Response> expectation) {
        return scenario(List.of(scenario), expectation);
    }

    @Override
    public HttpContractPathParamsStage<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation) {
        overrides.addScenario(scenarios, expectation);
        return this;
    }

    @Override
    public HttpContractPathParamsStage<T> targets(FuzzScenario scenario, Consumer<Response> expectation, List<TargetExpression<T>> targets) {
        return targets(List.of(scenario), expectation, targets);
    }

    @Override
    public HttpContractPathParamsStage<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, List<TargetExpression<T>> targets) {
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

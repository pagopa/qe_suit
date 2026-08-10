package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class HttpContractPayloadStage<T> implements HttpContractStages.PayloadStage<T> {
    private final HttpContractInvocationBuilder builder;
    private final ScopeOverrides overrides;

    HttpContractPayloadStage(HttpContractInvocationBuilder builder, ScopeOverrides overrides) {
        this.builder = builder;
        this.overrides = overrides;
    }

    @Override
    public HttpContractPayloadStage<T> scenario(FuzzScenario scenario, Consumer<Response> expectation) {
        return scenario(List.of(scenario), expectation);
    }

    @Override
    public HttpContractPayloadStage<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation) {
        overrides.addScenario(scenarios, expectation);
        return this;
    }

    @Override
    public HttpContractPayloadStage<T> targets(FuzzScenario scenario, Consumer<Response> expectation, TargetExpression<T>... targets) {
        return targets(List.of(scenario), expectation, targets);
    }

    @Override
    public HttpContractPayloadStage<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, TargetExpression<T>... targets) {
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

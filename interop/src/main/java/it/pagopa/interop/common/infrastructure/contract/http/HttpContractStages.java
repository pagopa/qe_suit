package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface HttpContractStages {

    interface TestsStage {
        Stream<DynamicTest> tests();
    }

    interface ApiCallStage extends TestsStage {
        <T> PayloadStage<T> payload(T payload);

        <T> PathParamsStage<T> pathParams(T pathParams);
    }

    interface PayloadStage<T> extends TestsStage {
        PayloadStage<T> scenario(FuzzScenario scenario, Consumer<Response> expectation);

        PayloadStage<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation);

        PayloadStage<T> targets(FuzzScenario scenario, Consumer<Response> expectation, TargetExpression<T>... targets);

        PayloadStage<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, TargetExpression<T>... targets);

        <P> PathParamsStage<P> pathParams(P pathParams);
    }

    interface PathParamsStage<T> extends TestsStage {
        PathParamsStage<T> scenario(FuzzScenario scenario, Consumer<Response> expectation);

        PathParamsStage<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation);

        PathParamsStage<T> targets(FuzzScenario scenario, Consumer<Response> expectation, TargetExpression<T>... targets);

        PathParamsStage<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, TargetExpression<T>... targets);

        <P> PayloadStage<P> payload(P payload);
    }
}

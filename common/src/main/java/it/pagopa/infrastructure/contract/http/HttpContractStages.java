package it.pagopa.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface HttpContractStages {

    interface TestsStage {
        Stream<DynamicTest> tests();
    }

    interface ApiCallStage extends TestsStage {
        default <T> PayloadStage<T> payload(T payload) {
            return payload(() -> payload);
        }

        <T> PayloadStage<T> payload(Supplier<T> payloadSupplier);

        default <T> PathParamsStage<T> pathParams(T pathParams) {
            return pathParams(() -> pathParams);
        }

        <T> PathParamsStage<T> pathParams(Supplier<T> pathParamsSupplier);
    }

    interface PayloadStage<T> extends TestsStage {
        PayloadStage<T> scenario(FuzzScenario scenario, Consumer<Response> expectation);

        PayloadStage<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation);

        PayloadStage<T> targets(FuzzScenario scenario, Consumer<Response> expectation, List<TargetExpression<T>> targets);

        PayloadStage<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, List<TargetExpression<T>> targets);

        default <P> PathParamsStage<P> pathParams(P pathParams) {
            return pathParams(() -> pathParams);
        }

        <P> PathParamsStage<P> pathParams(Supplier<P> pathParamsSupplier);
    }

    interface PathParamsStage<T> extends TestsStage {
        PathParamsStage<T> scenario(FuzzScenario scenario, Consumer<Response> expectation);

        PathParamsStage<T> scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation);

        PathParamsStage<T> targets(FuzzScenario scenario, Consumer<Response> expectation, List<TargetExpression<T>> targets);

        PathParamsStage<T> targets(List<FuzzScenario> scenarios, Consumer<Response> expectation, List<TargetExpression<T>> targets);

        default <P> PayloadStage<P> payload(P payload) {
            return payload(() -> payload);
        }

        <P> PayloadStage<P> payload(Supplier<P> payloadSupplier);
    }
}

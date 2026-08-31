package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;

import java.util.List;
import java.util.function.Consumer;

public interface HttpContractPolicy {

    Consumer<Response> success();

    Consumer<Response> expectationFor(FuzzScenario scenario);

    static Builder builder() {
        return new DefaultHttpContractPolicy.BuilderImpl();
    }

    interface Builder {

        Builder success(Consumer<Response> expectation);

        Builder successStatus(int status);

        Builder scenario(FuzzScenario scenario, Consumer<Response> expectation);

        Builder scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation);

        Builder scenarioStatus(FuzzScenario scenario, int status);

        Builder scenarioStatus(List<FuzzScenario> scenarios, int status);

        HttpContractPolicy build();
    }
}

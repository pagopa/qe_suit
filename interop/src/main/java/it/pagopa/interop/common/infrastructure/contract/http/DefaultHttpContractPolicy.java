package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class DefaultHttpContractPolicy implements HttpContractPolicy {

    private final Consumer<Response> success;
    private final EnumMap<FuzzScenario, Consumer<Response>> scenarioExpectations;

    private DefaultHttpContractPolicy(Consumer<Response> success, EnumMap<FuzzScenario, Consumer<Response>> scenarioExpectations) {
        this.success = success;
        this.scenarioExpectations = scenarioExpectations;
    }

    @Override
    public Consumer<Response> success() {
        return success;
    }

    @Override
    public Consumer<Response> expectationFor(FuzzScenario scenario) {
        Objects.requireNonNull(scenario, "scenario must not be null");
        Consumer<Response> expectation = scenarioExpectations.get(scenario);
        if (expectation == null) {
            throw new ContractHttpException("Missing expectation for scenario " + scenario);
        }
        return expectation;
    }

    static final class BuilderImpl implements HttpContractPolicy.Builder {
        private Consumer<Response> success;
        private final EnumMap<FuzzScenario, Consumer<Response>> scenarioExpectations = new EnumMap<>(FuzzScenario.class);

        @Override
        public HttpContractPolicy.Builder success(Consumer<Response> expectation) {
            this.success = Objects.requireNonNull(expectation, "success expectation must not be null");
            return this;
        }

        @Override
        public HttpContractPolicy.Builder successStatus(int status) {
            return success(response -> response.then().statusCode(status));
        }

        @Override
        public HttpContractPolicy.Builder scenario(FuzzScenario scenario, Consumer<Response> expectation) {
            Objects.requireNonNull(scenario, "scenario must not be null");
            Objects.requireNonNull(expectation, "scenario expectation must not be null");
            if (scenarioExpectations.putIfAbsent(scenario, expectation) != null) {
                throw new ContractHttpException("Duplicate scenario configuration: " + scenario);
            }
            return this;
        }

        @Override
        public HttpContractPolicy.Builder scenario(List<FuzzScenario> scenarios, Consumer<Response> expectation) {
            validateScenarioList(scenarios);
            Objects.requireNonNull(expectation, "scenario expectation must not be null");
            for (FuzzScenario scenario : scenarios) {
                scenario(scenario, expectation);
            }
            return this;
        }

        @Override
        public HttpContractPolicy.Builder scenarioStatus(FuzzScenario scenario, int status) {
            return scenario(scenario, response -> response.then().statusCode(status));
        }

        @Override
        public HttpContractPolicy.Builder scenarioStatus(List<FuzzScenario> scenarios, int status) {
            return scenario(scenarios, response -> response.then().statusCode(status));
        }

        @Override
        public HttpContractPolicy build() {
            if (success == null) {
                throw new ContractHttpException("Missing success expectation");
            }
            Set<FuzzScenario> missing = EnumSet.allOf(FuzzScenario.class);
            missing.removeAll(scenarioExpectations.keySet());
            if (!missing.isEmpty()) {
                throw new ContractHttpException("HttpContractPolicy is incomplete. Missing scenarios: " + missing);
            }
            return new DefaultHttpContractPolicy(success, new EnumMap<>(scenarioExpectations));
        }

        private void validateScenarioList(List<FuzzScenario> scenarios) {
            if (scenarios == null) {
                throw new ContractHttpException("scenarios must not be null");
            }
            if (scenarios.isEmpty()) {
                throw new ContractHttpException("scenarios must not be empty");
            }
            if (scenarios.stream().anyMatch(Objects::isNull)) {
                throw new ContractHttpException("scenarios must not contain null elements");
            }
        }
    }
}

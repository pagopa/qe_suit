package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ExpectationResolver<T> {

    private final Supplier<T> success;
    private final Function<FuzzScenario, T> scenarioPolicy;

    public ExpectationResolver(Supplier<T> success, Function<FuzzScenario, T> scenarioPolicy) {
        this.success = Objects.requireNonNull(success, "success must not be null");
        this.scenarioPolicy = Objects.requireNonNull(scenarioPolicy, "scenarioPolicy must not be null");
    }

    public Resolution<T> resolve(
            FuzzScenario scenario,
            Optional<T> targetOverride,
            Optional<T> scenarioOverride,
            Supplier<ContractValidity> validity
    ) {
        Objects.requireNonNull(scenario, "scenario must not be null");
        Objects.requireNonNull(targetOverride, "targetOverride must not be null");
        Objects.requireNonNull(scenarioOverride, "scenarioOverride must not be null");
        Objects.requireNonNull(validity, "validity must not be null");

        if (targetOverride.isPresent()) {
            return new Resolution<>(targetOverride.get(), ExpectationOrigin.TARGET_OVERRIDE);
        }
        if (scenarioOverride.isPresent()) {
            return new Resolution<>(scenarioOverride.get(), ExpectationOrigin.SCENARIO_OVERRIDE);
        }

        ContractValidity resolvedValidity = Objects.requireNonNull(validity.get(), "validity must not be null");
        if (resolvedValidity == ContractValidity.VALID) {
            return new Resolution<>(
                    Objects.requireNonNull(success.get(), "success expectation must not be null"),
                    ExpectationOrigin.INFERRED_VALID
            );
        }
        T expectation = Objects.requireNonNull(
                scenarioPolicy.apply(scenario),
                "scenario policy expectation must not be null"
        );
        ExpectationOrigin origin = resolvedValidity == ContractValidity.INVALID
                ? ExpectationOrigin.POLICY_INVALID
                : ExpectationOrigin.POLICY_UNKNOWN;
        return new Resolution<>(expectation, origin);
    }

    public record Resolution<T>(T expectation, ExpectationOrigin origin) {
        public Resolution {
            Objects.requireNonNull(expectation, "expectation must not be null");
            Objects.requireNonNull(origin, "origin must not be null");
        }
    }
}

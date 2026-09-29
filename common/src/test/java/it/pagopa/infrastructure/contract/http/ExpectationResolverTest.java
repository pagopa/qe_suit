package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpectationResolverTest {

    @Test
    void targetAndScenarioOverridesDoNotEvaluateValidity() {
        AtomicInteger validityCalls = new AtomicInteger();
        AtomicInteger successCalls = new AtomicInteger();
        ExpectationResolver<String> resolver = new ExpectationResolver<>(() -> {
            successCalls.incrementAndGet();
            return "success";
        }, scenario -> "policy");

        var target = resolver.resolve(
                FuzzScenario.REMOVED,
                Optional.of("target"),
                Optional.of("scenario"),
                () -> {
                    validityCalls.incrementAndGet();
                    return ContractValidity.INVALID;
                }
        );
        var scenario = resolver.resolve(
                FuzzScenario.REMOVED,
                Optional.empty(),
                Optional.of("scenario"),
                () -> {
                    validityCalls.incrementAndGet();
                    return ContractValidity.INVALID;
                }
        );

        assertEquals("target", target.expectation());
        assertEquals(ExpectationOrigin.TARGET_OVERRIDE, target.origin());
        assertEquals("scenario", scenario.expectation());
        assertEquals(ExpectationOrigin.SCENARIO_OVERRIDE, scenario.origin());
        assertEquals(0, validityCalls.get());
        assertEquals(0, successCalls.get());
    }

    @Test
    void preservesValiditySpecificPolicyOrigins() {
        ExpectationResolver<String> resolver = new ExpectationResolver<>(() -> "success", scenario -> "policy");

        var valid = resolver.resolve(FuzzScenario.REMOVED, Optional.empty(), Optional.empty(), () -> ContractValidity.VALID);
        var invalid = resolver.resolve(FuzzScenario.REMOVED, Optional.empty(), Optional.empty(), () -> ContractValidity.INVALID);
        var unknown = resolver.resolve(FuzzScenario.REMOVED, Optional.empty(), Optional.empty(), () -> ContractValidity.UNKNOWN);

        assertEquals(ExpectationOrigin.INFERRED_VALID, valid.origin());
        assertEquals(ExpectationOrigin.POLICY_INVALID, invalid.origin());
        assertEquals(ExpectationOrigin.POLICY_UNKNOWN, unknown.origin());
        assertEquals("success", valid.expectation());
        assertEquals("policy", invalid.expectation());
        assertEquals("policy", unknown.expectation());
    }
}

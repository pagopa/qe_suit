package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpContractValidatorPolicyTest {

    @Test
    void buildFailsWhenSuccessMissing() {
        assertThrows(ContractHttpException.class, () -> HttpContractPolicy.builder().build());
    }

    @Test
    void buildFailsWhenAnyScenarioMissing() {
        ContractHttpException exception = assertThrows(ContractHttpException.class, () -> {
            HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
            builder.scenarioStatus(FuzzScenario.REPLACED_WITH_NULL, 400);
            builder.build();
        });
        assertTrue(exception.getMessage().contains("Missing scenarios"));
    }

    @Test
    void buildSucceedsWhenPolicyIsComplete() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenarioStatus(scenario, 400);
        }
        HttpContractPolicy policy = builder.build();
        for (FuzzScenario scenario : EnumSet.allOf(FuzzScenario.class)) {
            assertDoesNotThrow(() -> policy.expectationFor(scenario));
        }
    }

    @Test
    void skippedScenariosDoNotRequireExpectations() {
        FuzzScenario skipped = FuzzScenario.REPLACED_WITH_LONG_STRING;
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenarioStatus(scenario, 400);
        }

        HttpContractPolicy policy = builder.skipScenario(skipped).build();

        assertTrue(policy.isSkipped(skipped));
        assertThrows(ContractHttpException.class, () -> policy.expectationFor(skipped));
        for (FuzzScenario scenario : FuzzScenario.values()) {
            if (scenario != skipped) {
                assertDoesNotThrow(() -> policy.expectationFor(scenario));
            }
        }
    }

    @Test
    void skipScenarioCanBeConfiguredBeforeOtherScenarioExpectations() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder()
                .successStatus(200)
                .skipScenario(FuzzScenario.REPLACED_WITH_LONG_STRING);
        for (FuzzScenario scenario : FuzzScenario.values()) {
            if (scenario != FuzzScenario.REPLACED_WITH_LONG_STRING) {
                builder.scenarioStatus(scenario, 400);
            }
        }

        assertTrue(builder.build().isSkipped(FuzzScenario.REPLACED_WITH_LONG_STRING));
    }

    @Test
    void duplicatesFailFast() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenarioStatus(scenario, 400);
        }
        assertThrows(ContractHttpException.class, () -> builder.scenarioStatus(FuzzScenario.REPLACED_WITH_NULL, 422));
    }

    @Test
    void conflictingAndDuplicateSkipConfigurationsFailFast() {
        HttpContractPolicy.Builder scenarioThenSkip = HttpContractPolicy.builder().successStatus(200)
                .scenarioStatus(FuzzScenario.REPLACED_WITH_NULL, 400);
        assertDoesNotThrow(() -> scenarioThenSkip.skipScenario(FuzzScenario.REPLACED_WITH_NULL));

        HttpContractPolicy.Builder skipThenScenario = HttpContractPolicy.builder().successStatus(200)
                .skipScenario(FuzzScenario.REPLACED_WITH_NULL);
        assertThrows(
                ContractHttpException.class,
                () -> skipThenScenario.scenarioStatus(FuzzScenario.REPLACED_WITH_NULL, 400)
        );
        assertThrows(
                ContractHttpException.class,
                () -> skipThenScenario.skipScenario(FuzzScenario.REPLACED_WITH_NULL)
        );
    }

    @Test
    void listOverloadValidatesInput() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
        assertThrows(ContractHttpException.class, () -> builder.scenario((List<FuzzScenario>) null, r -> {}));
        assertThrows(ContractHttpException.class, () -> builder.scenario(List.of(), r -> {}));
        assertThrows(ContractHttpException.class, () -> builder.scenario(java.util.Arrays.asList(FuzzScenario.REPLACED_WITH_NULL, null), r -> {}));
    }
}

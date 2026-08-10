package it.pagopa.interop.common.infrastructure.contract.http;

import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpContractPolicyTest {

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
    void duplicatesFailFast() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenarioStatus(scenario, 400);
        }
        assertThrows(ContractHttpException.class, () -> builder.scenarioStatus(FuzzScenario.REPLACED_WITH_NULL, 422));
    }

    @Test
    void listOverloadValidatesInput() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().successStatus(200);
        assertThrows(ContractHttpException.class, () -> builder.scenario((List<FuzzScenario>) null, r -> {}));
        assertThrows(ContractHttpException.class, () -> builder.scenario(List.of(), r -> {}));
        assertThrows(ContractHttpException.class, () -> builder.scenario(java.util.Arrays.asList(FuzzScenario.REPLACED_WITH_NULL, null), r -> {}));
    }
}

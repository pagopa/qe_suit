package it.pagopa.interop.common.infrastructure.http.contract.engine;

import it.pagopa.interop.new_arch.common.infrastructure.http.contract.FuzzVectors;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParamFuzzerGeneratorTest {

    @Test
    void generateFuzzCases_for_string_param_adds_missing_and_all_string_vectors() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(
                Map.of("tenantId", "tenant-01")
        );

        assertEquals(1 + FuzzVectors.STRINGS.size(), cases.size());
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.REQUIRED_MISSING));
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.XSS));
    }

    @Test
    void generateFuzzCases_for_integer_param_adds_missing_and_all_integer_vectors() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(
                Map.of("offset", 0)
        );

        assertEquals(1 + FuzzVectors.INTEGERS.size(), cases.size());
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.NOT_A_INTEGER));
    }

    @Test
    void generateFuzzCases_for_uuid_param_adds_missing_uuid_and_string_vectors() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(
                Map.of("agreementId", UUID.randomUUID())
        );

        int expectedCount = 1 + FuzzVectors.UUIDS.size() + FuzzVectors.STRINGS.size();
        assertEquals(expectedCount, cases.size());
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.UUID_NIL));
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.EMPTY_STRING));
    }

    @Test
    void required_missing_case_removes_param_from_raw_inputs() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(
                Map.of("tenantId", "tenant-01", "offset", 0)
        );

        ParamFuzzerGenerator.ParamFuzzedCase missingTenant = cases.stream()
                .filter(c -> c.paramName().equals("tenantId") && c.fuzzId() == FuzzVectors.FuzzId.REQUIRED_MISSING)
                .findFirst()
                .orElseThrow();

        assertFalse(missingTenant.rawInputs().containsKey("tenantId"));
        assertTrue(missingTenant.rawInputs().containsKey("offset"));
        assertEquals(400, missingTenant.expectedStatus());
    }

    @Test
    void boolean_param_generates_only_required_missing_case() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(
                Map.of("verbose", false)
        );

        assertEquals(1, cases.size());
        assertEquals(FuzzVectors.FuzzId.REQUIRED_MISSING, cases.get(0).fuzzId());
    }

    @Test
    void empty_input_generates_no_cases() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(Map.of());

        assertTrue(cases.isEmpty());
    }
}


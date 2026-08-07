package it.pagopa.interop.common.infrastructure.http.contract.engine;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.common.infrastructure.http.contract.FuzzVectors;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuzzerGeneratorTest {

    private record StringDto(String name) implements Serializable {
    }

    private record IntDto(Integer count) implements Serializable {
    }

    private record UuidDto(UUID id) implements Serializable {
    }

    private record MixedDto(String name, UUID id, Integer count) implements Serializable {
    }

    @Test
    void generateFuzzCases_for_string_adds_missing_and_all_string_vectors() {
        List<FuzzerGenerator.FuzzedCase> cases = FuzzerGenerator.generateFuzzCases(new StringDto("valid"));

        assertEquals(1 + FuzzVectors.STRINGS.size(), cases.size());
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.REQUIRED_MISSING));
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.SQL_INJECTION));
    }

    @Test
    void generateFuzzCases_for_integer_adds_missing_and_all_integer_vectors() {
        List<FuzzerGenerator.FuzzedCase> cases = FuzzerGenerator.generateFuzzCases(new IntDto(1));

        assertEquals(1 + FuzzVectors.INTEGERS.size(), cases.size());
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.NOT_A_INTEGER));
    }

    @Test
    void generateFuzzCases_for_uuid_adds_missing_uuid_and_string_vectors() {
        List<FuzzerGenerator.FuzzedCase> cases = FuzzerGenerator.generateFuzzCases(new UuidDto(UUID.randomUUID()));

        int expectedCount = 1 + FuzzVectors.UUIDS.size() + FuzzVectors.STRINGS.size();
        assertEquals(expectedCount, cases.size());
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.UUID_MALFORMED));
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.EMPTY_STRING));
    }

    @Test
    void required_missing_case_removes_field_from_raw_body() {
        List<FuzzerGenerator.FuzzedCase> cases = FuzzerGenerator.generateFuzzCases(new StringDto("valid"));

        FuzzerGenerator.FuzzedCase missingCase = cases.stream()
                .filter(c -> c.fuzzId() == FuzzVectors.FuzzId.REQUIRED_MISSING)
                .findFirst()
                .orElseThrow();

        assertFalse(missingCase.rawBody().containsKey("name"));
        assertEquals(400, missingCase.expectedStatus());
    }

    @Test
    void fuzzed_case_changes_only_target_field() {
        UUID id = UUID.randomUUID();
        MixedDto dto = new MixedDto("original", id, 10);

        List<FuzzerGenerator.FuzzedCase> cases = FuzzerGenerator.generateFuzzCases(dto);

        FuzzerGenerator.FuzzedCase sqlOnName = cases.stream()
                .filter(c -> c.fieldName().equals("name") && c.fuzzId() == FuzzVectors.FuzzId.SQL_INJECTION)
                .findFirst()
                .orElseThrow();

        assertEquals("' OR '1'='1", sqlOnName.rawBody().get("name"));
        assertTrue(
                id.equals(sqlOnName.rawBody().get("id")) || id.toString().equals(sqlOnName.rawBody().get("id"))
        );
        assertEquals(10, sqlOnName.rawBody().get("count"));
    }

    @Test
    void generateFuzzCases_for_openapi_payload_ignores_static_metadata_fields() {
        AgreementPayload payload = new AgreementPayload()
                .eserviceId(UUID.randomUUID())
                .descriptorId(UUID.randomUUID());

        List<FuzzerGenerator.FuzzedCase> cases = FuzzerGenerator.generateFuzzCases(payload);

        assertTrue(cases.stream().noneMatch(c -> c.fieldName().startsWith("JSON_PROPERTY_")));
        assertTrue(cases.stream().noneMatch(c -> "serialVersionUID".equals(c.fieldName())));
        assertTrue(cases.stream().noneMatch(c -> c.rawBody().containsKey("serialVersionUID")));
        assertTrue(cases.stream().noneMatch(c -> c.rawBody().keySet().stream().anyMatch(k -> k.startsWith("JSON_PROPERTY_"))));
    }

    @Test
    void generateFuzzCases_for_params_adds_missing_and_string_vectors() {
        List<ParamFuzzerGenerator.ParamFuzzedCase> cases = ParamFuzzerGenerator.generateFuzzCases(
                Map.of("tenantId", "tenant-01")
        );

        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.REQUIRED_MISSING));
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.EMPTY_STRING));
        assertTrue(cases.stream().anyMatch(c -> c.fuzzId() == FuzzVectors.FuzzId.SQL_INJECTION));
    }
}

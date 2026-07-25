package it.pagopa.interop.common.infrastructure.http.contract.engine;

import it.pagopa.interop.common.infrastructure.http.contract.FuzzVectors;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FuzzTestNameFormatterTest {

    @Test
    void validPayloadName_contains_expected_status() {
        String name = FuzzTestNameFormatter.validPayloadName(201);

        assertTrue(name.contains("201"));
    }

    @Test
    void fuzzName_formats_empty_string_value_as_STRINGA_VUOTA() {
        FuzzerGenerator.FuzzedCase fuzzedCase = new FuzzerGenerator.FuzzedCase(
                "name",
                FuzzVectors.FuzzId.EMPTY_STRING,
                "",
                400,
                "desc",
                Map.of()
        );

        String testName = FuzzTestNameFormatter.fuzzName(fuzzedCase, 400);

        assertTrue(testName.contains("STRINGA_VUOTA"));
        assertTrue(testName.contains("name"));
        assertTrue(testName.contains("400"));
    }

    @Test
    void fuzzName_formats_blank_string_as_SOLO_SPAZI() {
        FuzzerGenerator.FuzzedCase fuzzedCase = new FuzzerGenerator.FuzzedCase(
                "name",
                FuzzVectors.FuzzId.BLANK_STRING,
                "   ",
                400,
                "desc",
                Map.of()
        );

        String testName = FuzzTestNameFormatter.fuzzName(fuzzedCase, 400);

        assertTrue(testName.contains("SOLO_SPAZI"));
    }

    @Test
    void fuzzName_formats_null_as_literal_null() {
        FuzzerGenerator.FuzzedCase fuzzedCase = new FuzzerGenerator.FuzzedCase(
                "name",
                FuzzVectors.FuzzId.REQUIRED_MISSING,
                null,
                400,
                "desc",
                Map.of()
        );

        String testName = FuzzTestNameFormatter.fuzzName(fuzzedCase, 400);

        assertTrue(testName.contains("null"));
    }

    @Test
    void paramFuzzName_contains_param_name_fuzz_id_and_status() {
        ParamFuzzerGenerator.ParamFuzzedCase paramCase = new ParamFuzzerGenerator.ParamFuzzedCase(
                "tenantId",
                FuzzVectors.FuzzId.SQL_INJECTION,
                "' OR '1'='1",
                403,
                Map.of()
        );

        String testName = FuzzTestNameFormatter.paramFuzzName(paramCase, 403);

        assertTrue(testName.contains("tenantId"));
        assertTrue(testName.contains("SQL_INJECTION"));
        assertTrue(testName.contains("403"));
    }
}


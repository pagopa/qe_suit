package it.pagopa.interop.common.infrastructure.http.contract.engine;

final class FuzzTestNameFormatter {

    private FuzzTestNameFormatter() {
    }

    static String validPayloadName(int expectedStatus) {
        return "Valid payload -> atteso: " + expectedStatus;
    }

    static String fuzzName(FuzzerGenerator.FuzzedCase fuzzCase, int expectedStatus) {
        return String.format(
                "Fuzz [%s] -> campo [%s] con [%s] -> atteso: %d",
                fuzzCase.fuzzId(),
                fuzzCase.fieldName(),
                displayValue(fuzzCase.fuzzedValue()),
                expectedStatus
        );
    }

    static String paramFuzzName(ParamFuzzerGenerator.ParamFuzzedCase c, int expectedStatus) {
        return String.format(
                "Params [%s] -> param [%s] con [%s] -> atteso: %d",
                c.fuzzId(),
                c.paramName(),
                displayValue(c.fuzzedValue()),
                expectedStatus
        );
    }

    private static String displayValue(Object value) {
        if (value == null) {
            return "null";
        }

        String stringValue = value.toString();

        if (stringValue.isEmpty()) {
            return "STRINGA_VUOTA";
        }

        if (stringValue.isBlank()) {
            return "SOLO_SPAZI";
        }

        return stringValue;
    }
}
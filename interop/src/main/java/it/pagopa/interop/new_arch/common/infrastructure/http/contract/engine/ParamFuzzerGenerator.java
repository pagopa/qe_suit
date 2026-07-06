package it.pagopa.interop.new_arch.common.infrastructure.http.contract.engine;

import it.pagopa.interop.new_arch.common.infrastructure.http.contract.FuzzVectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class ParamFuzzerGenerator {

    private static final int REQUIRED_PARAM_MISSING_STATUS = 400;

    record ParamFuzzedCase(
            String paramName,
            FuzzVectors.FuzzId fuzzId,
            Object fuzzedValue,
            int expectedStatus,
            Map<String, Object> rawInputs
    ) {}

    /**
     * Per ogni param nella mappa valida genera:
     * - required missing (campo rimosso dalla mappa)
     * - vettori appropriati al tipo del valore (String -> STRINGS, Integer -> INTEGERS, UUID -> UUIDS + STRINGS)
     * I tipi Boolean e altri primitivi non vengono fuzzati (non applicabile).
     */
    static List<ParamFuzzedCase> generateFuzzCases(Map<String, Object> validInputs) {
        List<ParamFuzzedCase> cases = new ArrayList<>();

        for (Map.Entry<String, Object> entry : validInputs.entrySet()) {
            String paramName = entry.getKey();
            Object validValue = entry.getValue();

            injectMissingRequired(cases, validInputs, paramName);

            if (validValue instanceof String) {
                injectVectors(cases, validInputs, paramName, FuzzVectors.STRINGS);
            } else if (validValue instanceof Integer) {
                injectVectors(cases, validInputs, paramName, FuzzVectors.INTEGERS);
            } else if (validValue instanceof UUID) {
                injectVectors(cases, validInputs, paramName, FuzzVectors.UUIDS);
                injectVectors(cases, validInputs, paramName, FuzzVectors.STRINGS);
            }
        }

        return cases;
    }

    private static void injectMissingRequired(List<ParamFuzzedCase> cases,
                                              Map<String, Object> validInputs,
                                              String paramName) {
        Map<String, Object> fuzzed = new HashMap<>(validInputs);
        fuzzed.remove(paramName);

        cases.add(new ParamFuzzedCase(
                paramName,
                FuzzVectors.FuzzId.REQUIRED_MISSING,
                "<ABSENT>",
                REQUIRED_PARAM_MISSING_STATUS,
                fuzzed
        ));
    }

    private static void injectVectors(List<ParamFuzzedCase> cases,
                                      Map<String, Object> validInputs,
                                      String paramName,
                                      List<? extends FuzzVectors<?>> vectors) {
        for (FuzzVectors<?> vector : vectors) {
            Map<String, Object> fuzzed = new HashMap<>(validInputs);
            fuzzed.put(paramName, vector.value());

            cases.add(new ParamFuzzedCase(
                    paramName,
                    vector.id(),
                    vector.value(),
                    vector.expectedStatus(),
                    fuzzed
            ));
        }
    }
}
package it.pagopa.interop.new_arch.common.infrastructure.http.contract.engine;

import it.pagopa.interop.new_arch.common.infrastructure.http.contract.FuzzVectors;

import java.util.HashMap;
import java.util.Map;

final class FuzzExpectationResolver {

    private final Map<RuleKey, Integer> byFieldAndAttack = new HashMap<>();
    private final Map<String, Integer> byField = new HashMap<>();
    private final Map<FuzzVectors.FuzzId, Integer> byAttack = new HashMap<>();

    void putPrecise(String fieldName, FuzzVectors.FuzzId fuzzId, int expectedStatus) {
        byFieldAndAttack.put(new RuleKey(fieldName, fuzzId), expectedStatus);
    }

    void putField(String fieldName, int expectedStatus) {
        byField.put(fieldName, expectedStatus);
    }

    void putAttack(FuzzVectors.FuzzId fuzzId, int expectedStatus) {
        byAttack.put(fuzzId, expectedStatus);
    }

    /** Usato per i body fuzz cases (riceve un FuzzedCase completo) */
    int resolve(FuzzerGenerator.FuzzedCase fuzzCase) {
        return resolveParam(fuzzCase.fieldName(), fuzzCase.fuzzId(), fuzzCase.expectedStatus());
    }

    /** Usato per i params fuzz cases (riceve i campi separati) */
    int resolveParam(String name, FuzzVectors.FuzzId fuzzId, int defaultStatus) {
        RuleKey preciseRule = new RuleKey(name, fuzzId);

        if (byFieldAndAttack.containsKey(preciseRule)) {
            return byFieldAndAttack.get(preciseRule);
        }

        if (byField.containsKey(name)) {
            return byField.get(name);
        }

        return byAttack.getOrDefault(fuzzId, defaultStatus);
    }

    private record RuleKey(String fieldName, FuzzVectors.FuzzId fuzzId) {
    }
}
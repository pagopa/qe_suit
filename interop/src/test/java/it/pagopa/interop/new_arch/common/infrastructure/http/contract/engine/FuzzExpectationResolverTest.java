package it.pagopa.interop.new_arch.common.infrastructure.http.contract.engine;

import it.pagopa.interop.new_arch.common.infrastructure.http.contract.FuzzVectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuzzExpectationResolverTest {

    private FuzzExpectationResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new FuzzExpectationResolver();
    }

    @Test
    void resolveParam_returns_precise_rule_when_present() {
        resolver.putPrecise("name", FuzzVectors.FuzzId.EMPTY_STRING, 422);
        resolver.putField("name", 400);
        resolver.putAttack(FuzzVectors.FuzzId.EMPTY_STRING, 500);

        int result = resolver.resolveParam("name", FuzzVectors.FuzzId.EMPTY_STRING, 999);

        assertEquals(422, result);
    }

    @Test
    void resolveParam_returns_field_rule_when_precise_missing() {
        resolver.putField("name", 400);
        resolver.putAttack(FuzzVectors.FuzzId.EMPTY_STRING, 500);

        int result = resolver.resolveParam("name", FuzzVectors.FuzzId.EMPTY_STRING, 999);

        assertEquals(400, result);
    }

    @Test
    void resolveParam_returns_attack_rule_when_precise_and_field_missing() {
        resolver.putAttack(FuzzVectors.FuzzId.SQL_INJECTION, 403);

        int result = resolver.resolveParam("any", FuzzVectors.FuzzId.SQL_INJECTION, 999);

        assertEquals(403, result);
    }

    @Test
    void resolveParam_returns_default_when_no_rule_matches() {
        int result = resolver.resolveParam("any", FuzzVectors.FuzzId.XSS, 400);

        assertEquals(400, result);
    }

    @Test
    void resolve_delegates_to_same_priority_logic_for_body_case() {
        resolver.putPrecise("name", FuzzVectors.FuzzId.EMPTY_STRING, 422);

        FuzzerGenerator.FuzzedCase fuzzedCase = new FuzzerGenerator.FuzzedCase(
                "name",
                FuzzVectors.FuzzId.EMPTY_STRING,
                "",
                400,
                "desc",
                java.util.Map.of()
        );

        int result = resolver.resolve(fuzzedCase);

        assertEquals(422, result);
    }
}


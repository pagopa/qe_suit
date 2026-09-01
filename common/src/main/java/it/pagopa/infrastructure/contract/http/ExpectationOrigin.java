package it.pagopa.infrastructure.contract.http;

enum ExpectationOrigin {
    TARGET_OVERRIDE,
    SCENARIO_OVERRIDE,
    INFERRED_VALID,
    POLICY_INVALID,
    POLICY_UNKNOWN
}

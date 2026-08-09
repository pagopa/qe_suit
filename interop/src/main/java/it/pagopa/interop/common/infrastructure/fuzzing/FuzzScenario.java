package it.pagopa.interop.common.infrastructure.fuzzing;

public enum FuzzScenario {
    NULL,
    MISSING,

    EMPTY_STRING,
    BLANK_STRING,
    LONG_STRING,

    SQL_INJECTION,
    XSS,

    WRONG_TYPE_STRING,
    WRONG_TYPE_NUMBER,
    WRONG_TYPE_DECIMAL,

    ZERO,
    NEGATIVE,
    MIN_VALUE,
    MAX_VALUE,

    MALFORMED_UUID,
    NIL_UUID,

    UNKNOWN_ENUM
}

package it.pagopa.interop.common.infrastructure.http.contract;

import java.util.List;

public record FuzzVectors<T>(FuzzId id, T value, int expectedStatus, String description) {

    public enum FuzzId {
        REQUIRED_MISSING,
        EMPTY_STRING,
        BLANK_STRING,
        NOT_A_STRING,
        NOT_A_INTEGER,
        CUSTOM_STRING,
        BUFFER_OVERFLOW,
        SQL_INJECTION,
        XSS,
        CUSTOM_INTEGER,
        UUID_MALFORMED,
        UUID_NIL
    }

    public static final List<FuzzVectors<?>> STRINGS = List.of(
            new FuzzVectors<>(FuzzId.EMPTY_STRING, "", 400, "Stringa vuota su campo obbligatorio"),
            new FuzzVectors<>(FuzzId.BLANK_STRING, "   ", 400, "Solo spazi vuoti"),
            new FuzzVectors<>(FuzzId.BUFFER_OVERFLOW, "A".repeat(5000), 413, "Payload troppo grande / Buffer Overflow"),
            new FuzzVectors<>(FuzzId.SQL_INJECTION, "' OR '1'='1", 403, "SQL Injection - Atteso Forbidden"),
            new FuzzVectors<>(FuzzId.XSS, "<script>alert(1)</script>", 403, "XSS - Atteso Forbidden"),
            new FuzzVectors<>(FuzzId.NOT_A_STRING, 124, 400, "Integer su un campo String")
    );

    public static final List<FuzzVectors<?>> INTEGERS = List.of(
            new FuzzVectors<>(FuzzId.NOT_A_INTEGER, 124.23, 400, "Float su un campo Integer")
    );

    public static final List<FuzzVectors<String>> UUIDS = List.of(
            new FuzzVectors<>(FuzzId.UUID_MALFORMED, "not-a-valid-uuid-12345", 400, "Formato UUID palesemente malformato"),
            new FuzzVectors<>(FuzzId.UUID_NIL, "00000000-0000-0000-0000-000000000000", 400, "Nil UUID (tutti zeri) - Spesso manda in errore i vincoli di integrità del DB")
    );

    public static FuzzVectors<String> stringValue(String value, int expectedStatus, String description) {
        return new FuzzVectors<>(
                FuzzId.CUSTOM_STRING,
                value,
                expectedStatus,
                description
        );
    }

    public static FuzzVectors<Integer> integerValue(int value, int expectedStatus, String description) {
        return new FuzzVectors<>(
                FuzzId.CUSTOM_INTEGER,
                value,
                expectedStatus,
                description
        );
    }
}
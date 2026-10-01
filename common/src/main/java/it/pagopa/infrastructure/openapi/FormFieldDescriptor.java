package it.pagopa.infrastructure.openapi;

import java.util.Objects;

/**
 * A single fuzzable field of a non JSON request body
 * ({@code multipart/form-data} or {@code application/x-www-form-urlencoded}).
 * Binary fields are not fuzzable and are never described here.
 */
public record FormFieldDescriptor(
        String name,
        Class<?> javaType,
        String schema
) {
    public FormFieldDescriptor {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(javaType, "javaType must not be null");
        Objects.requireNonNull(schema, "schema must not be null");
    }
}

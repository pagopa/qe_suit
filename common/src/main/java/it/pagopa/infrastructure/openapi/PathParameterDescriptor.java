package it.pagopa.infrastructure.openapi;

import java.util.Objects;

public record PathParameterDescriptor(
        String name,
        Class<?> javaType,
        String schema
) {
    public PathParameterDescriptor {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(javaType, "javaType must not be null");
        Objects.requireNonNull(schema, "schema must not be null");
    }
}

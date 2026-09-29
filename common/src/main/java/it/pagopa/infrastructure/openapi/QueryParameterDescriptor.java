package it.pagopa.infrastructure.openapi;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A single fuzzable query parameter of an operation.
 * <p>
 * {@code required} comes from the OpenAPI contract and is the only metadata driving
 * the validity of {@code REMOVED} and {@code REPLACED_WITH_NULL} scenarios: query parameters
 * have no generated DTO carrying validation annotations.
 */
public record QueryParameterDescriptor(
        String name,
        Type javaType,
        boolean required,
        String schema
) {
    public QueryParameterDescriptor {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(javaType, "javaType must not be null");
        Objects.requireNonNull(schema, "schema must not be null");
    }
}

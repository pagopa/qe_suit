package it.pagopa.infrastructure.openapi;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record DiscoveredOperation(
        String operationId,
        String httpMethod,
        String path,
        Optional<Type> requestBodyType,
        List<PathParameterDescriptor> pathParameters
) {
    public DiscoveredOperation {
        Objects.requireNonNull(operationId, "operationId must not be null");
        Objects.requireNonNull(httpMethod, "httpMethod must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(requestBodyType, "requestBodyType must not be null");
        pathParameters = List.copyOf(Objects.requireNonNull(pathParameters, "pathParameters must not be null"));
    }
}

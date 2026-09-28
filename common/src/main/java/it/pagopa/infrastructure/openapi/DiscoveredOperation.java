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
        List<FormFieldDescriptor> requestBodyFormFields,
        List<PathParameterDescriptor> pathParameters
) {
    public DiscoveredOperation {
        Objects.requireNonNull(operationId, "operationId must not be null");
        Objects.requireNonNull(httpMethod, "httpMethod must not be null");
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(requestBodyType, "requestBodyType must not be null");
        requestBodyFormFields = List.copyOf(
                Objects.requireNonNull(requestBodyFormFields, "requestBodyFormFields must not be null")
        );
        pathParameters = List.copyOf(Objects.requireNonNull(pathParameters, "pathParameters must not be null"));
        if (requestBodyType.isPresent() && !requestBodyFormFields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Operation " + operationId + " cannot declare both a JSON and a form request body"
            );
        }
    }

    public DiscoveredOperation(
            String operationId,
            String httpMethod,
            String path,
            Optional<Type> requestBodyType,
            List<PathParameterDescriptor> pathParameters
    ) {
        this(operationId, httpMethod, path, requestBodyType, List.of(), pathParameters);
    }
}

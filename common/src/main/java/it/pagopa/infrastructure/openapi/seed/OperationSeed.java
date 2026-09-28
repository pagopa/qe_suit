package it.pagopa.infrastructure.openapi.seed;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public record OperationSeed(
        Optional<Object> requestBody,
        Map<String, Object> pathParameters
) {
    public OperationSeed {
        requestBody = Optional.ofNullable(requestBody).orElse(Optional.empty());
        pathParameters = Collections.unmodifiableMap(new LinkedHashMap<>(pathParameters));
    }
}

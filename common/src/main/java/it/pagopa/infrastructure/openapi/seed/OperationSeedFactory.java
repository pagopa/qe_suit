package it.pagopa.infrastructure.openapi.seed;

import it.pagopa.infrastructure.openapi.DiscoveredOperation;
import it.pagopa.infrastructure.openapi.FormFieldDescriptor;
import it.pagopa.infrastructure.openapi.PathParameterDescriptor;
import it.pagopa.infrastructure.openapi.QueryParameterDescriptor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class OperationSeedFactory {

    private final DeterministicSeedFactory seedFactory;

    public OperationSeedFactory(DeterministicSeedFactory seedFactory) {
        this.seedFactory = Objects.requireNonNull(seedFactory, "seedFactory must not be null");
    }

    public OperationSeed create(DiscoveredOperation operation) {
        Objects.requireNonNull(operation, "operation must not be null");
        Optional<Object> body = operation.requestBodyType().map(seedFactory::create);
        if (body.isEmpty() && !operation.requestBodyFormFields().isEmpty()) {
            Map<String, Object> formBody = new LinkedHashMap<>();
            for (FormFieldDescriptor field : operation.requestBodyFormFields()) {
                formBody.put(field.name(), seedFactory.create(field.javaType()));
            }
            body = Optional.of(formBody);
        }
        Map<String, Object> pathParameters = new LinkedHashMap<>();
        for (PathParameterDescriptor parameter : operation.pathParameters()) {
            pathParameters.put(parameter.name(), seedFactory.create(parameter.javaType()));
        }
        Map<String, Object> queryParameters = new LinkedHashMap<>();
        for (QueryParameterDescriptor parameter : operation.queryParameters()) {
            queryParameters.put(parameter.name(), seedFactory.create(parameter.javaType()));
        }
        return new OperationSeed(body, pathParameters, queryParameters);
    }
}

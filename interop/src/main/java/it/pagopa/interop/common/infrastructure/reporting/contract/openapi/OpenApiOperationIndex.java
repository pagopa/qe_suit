package it.pagopa.interop.common.infrastructure.reporting.contract.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.util.HashMap;
import java.util.Map;

public class OpenApiOperationIndex {

    private final Map<String, String> operationTargets = new HashMap<>();

    public OpenApiOperationIndex(String openapiLocation) {
        OpenAPI openAPI = loadOpenApi(openapiLocation);
        Paths paths = openAPI.getPaths();
        if (paths == null || paths.isEmpty()) {
            throw new IllegalStateException("OpenAPI spec contains no paths: " + openapiLocation);
        }
        paths.forEach((path, pathItem) -> indexPathItem(path, pathItem));
    }

    public String resolve(String operationId) {
        String target = operationTargets.get(operationId);
        if (target == null) {
            throw new IllegalStateException("OpenAPI operationId not found: " + operationId);
        }
        return target;
    }

    private void indexPathItem(String path, PathItem pathItem) {
        if (pathItem == null || pathItem.readOperationsMap() == null) {
            return;
        }
        for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry : pathItem.readOperationsMap().entrySet()) {
            Operation operation = operationEntry.getValue();
            String operationId = operation == null ? null : operation.getOperationId();
            if (operationId == null || operationId.isBlank()) {
                continue;
            }
            String target = operationEntry.getKey().name() + " " + path;
            String existing = operationTargets.putIfAbsent(operationId, target);
            if (existing != null) {
                throw new IllegalStateException("Duplicate operationId in OpenAPI spec: " + operationId);
            }
        }
    }

    private OpenAPI loadOpenApi(String openapiLocation) {
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        SwaggerParseResult parseResult = new OpenAPIV3Parser().readLocation(openapiLocation, null, options);
        OpenAPI openAPI = parseResult == null ? null : parseResult.getOpenAPI();
        if (openAPI == null) {
            throw new IllegalStateException("Unable to load OpenAPI spec from: " + openapiLocation + ". Messages: " + (parseResult == null ? "[]" : parseResult.getMessages()));
        }
        return openAPI;
    }
}

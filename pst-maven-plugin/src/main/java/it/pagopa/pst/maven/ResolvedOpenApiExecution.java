package it.pagopa.pst.maven;

import java.util.Objects;

/**
 * Values resolved from one {@code openapi-generator-maven-plugin} execution of the effective project model.
 */
public record ResolvedOpenApiExecution(
        String executionId,
        String openApiLocation,
        String apiPackage,
        String modelPackage
) {
    public ResolvedOpenApiExecution {
        Objects.requireNonNull(executionId, "executionId must not be null");
        Objects.requireNonNull(openApiLocation, "openApiLocation must not be null");
        Objects.requireNonNull(apiPackage, "apiPackage must not be null");
        Objects.requireNonNull(modelPackage, "modelPackage must not be null");
    }
}

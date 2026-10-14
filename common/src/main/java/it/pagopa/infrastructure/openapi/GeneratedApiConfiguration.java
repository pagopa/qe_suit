package it.pagopa.infrastructure.openapi;

import java.util.Objects;

public record GeneratedApiConfiguration(
        String codegenId,
        String openApiLocation,
        String apiPackage,
        String modelPackage
) {
    public GeneratedApiConfiguration {
        requireText(codegenId, "codegenId");
        requireText(openApiLocation, "openApiLocation");
        requireText(apiPackage, "apiPackage");
        requireText(modelPackage, "modelPackage");
    }

    private static void requireText(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}

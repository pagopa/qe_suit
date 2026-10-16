package it.pagopa.infrastructure.pst.launcher;

import it.pagopa.infrastructure.fuzzing.FuzzingProfile;
import it.pagopa.infrastructure.openapi.GeneratedApiConfiguration;

import java.nio.file.Path;
import java.util.Objects;

public record PstGenerationRequest(
        GeneratedApiConfiguration apiConfiguration,
        Path configPath,
        Path outputPath,
        String title,
        FuzzingProfile fuzzingProfile
) {
    public PstGenerationRequest {
        Objects.requireNonNull(apiConfiguration, "apiConfiguration must not be null");
        Objects.requireNonNull(configPath, "configPath must not be null");
        Objects.requireNonNull(outputPath, "outputPath must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(fuzzingProfile, "fuzzingProfile must not be null");
    }
}

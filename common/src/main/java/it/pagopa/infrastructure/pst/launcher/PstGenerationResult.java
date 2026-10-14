package it.pagopa.infrastructure.pst.launcher;

import java.nio.file.Path;
import java.util.Objects;

public record PstGenerationResult(Path reportPath, int operationCount, int scenarioCount) {
    public PstGenerationResult {
        Objects.requireNonNull(reportPath, "reportPath must not be null");
    }
}

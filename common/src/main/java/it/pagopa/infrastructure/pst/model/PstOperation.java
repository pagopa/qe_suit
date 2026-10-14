package it.pagopa.infrastructure.pst.model;

import java.util.List;
import java.util.Objects;

public record PstOperation(
        String operationId,
        String httpMethod,
        String path,
        List<PstScenario> scenarios
) {
    public PstOperation {
        Objects.requireNonNull(operationId, "operationId must not be null");
        Objects.requireNonNull(httpMethod, "httpMethod must not be null");
        Objects.requireNonNull(path, "path must not be null");
        scenarios = List.copyOf(Objects.requireNonNull(scenarios, "scenarios must not be null"));
    }
}

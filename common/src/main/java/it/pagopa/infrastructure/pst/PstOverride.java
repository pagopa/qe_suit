package it.pagopa.infrastructure.pst;

import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;

import java.util.Objects;

public record PstOverride(
        String operationId,
        RequestScope scope,
        NodePath target,
        FuzzScenario scenario,
        int status
) {
    public PstOverride {
        if (operationId == null || operationId.isBlank()) {
            throw new PstConfigurationException("override operationId must not be blank");
        }
        Objects.requireNonNull(scope, "override scope must not be null");
        Objects.requireNonNull(target, "override target must not be null");
        Objects.requireNonNull(scenario, "override scenario must not be null");
        PstConfig.validateStatus(status, "override status");
    }
}

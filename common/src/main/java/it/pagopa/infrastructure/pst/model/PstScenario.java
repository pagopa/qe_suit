package it.pagopa.infrastructure.pst.model;

import it.pagopa.infrastructure.contract.http.ContractValidity;
import it.pagopa.infrastructure.contract.http.ExpectationOrigin;
import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;

import java.util.Objects;

public record PstScenario(
        RequestScope scope,
        NodePath target,
        FuzzScenario scenario,
        ContractValidity validity,
        int expectedStatus,
        ExpectationOrigin expectationOrigin
) {
    public PstScenario {
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(scenario, "scenario must not be null");
        Objects.requireNonNull(validity, "validity must not be null");
        Objects.requireNonNull(expectationOrigin, "expectationOrigin must not be null");
        if (expectedStatus < 100 || expectedStatus > 599) {
            throw new IllegalArgumentException("expectedStatus must be between 100 and 599");
        }
    }
}

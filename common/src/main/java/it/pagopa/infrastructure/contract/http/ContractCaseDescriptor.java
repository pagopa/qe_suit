package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;

record ContractCaseDescriptor(
        RequestScope scope,
        NodePath target,
        FuzzScenario scenario
) {
}

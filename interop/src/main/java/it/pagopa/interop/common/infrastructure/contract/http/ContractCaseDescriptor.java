package it.pagopa.interop.common.infrastructure.contract.http;

import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;

record ContractCaseDescriptor(
        RequestScope scope,
        NodePath target,
        FuzzScenario scenario
) {
}

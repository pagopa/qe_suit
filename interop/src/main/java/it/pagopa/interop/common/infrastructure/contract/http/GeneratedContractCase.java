package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;

import java.util.function.Consumer;

record GeneratedContractCase(
        RequestScope scope,
        NodePath target,
        FuzzMutation mutation,
        Consumer<Response> expectation,
        ExpectationOrigin expectationOrigin
) {
    ContractCaseDescriptor descriptor() {
        return new ContractCaseDescriptor(scope, target, mutation.scenario());
    }
}

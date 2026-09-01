package it.pagopa.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.objectgraph.NodePath;

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

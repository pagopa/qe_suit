package it.pagopa.interop.common.infrastructure.contract.http;

import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;

import java.util.function.Consumer;

record GeneratedContractCase(
        RequestScope scope,
        NodePath target,
        FuzzCase fuzzCase,
        HttpContractRequest request,
        Consumer<Response> expectation,
        ExpectationOrigin expectationOrigin
) {
}

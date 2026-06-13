package it.pagopa.interop.common.template.rest;

import it.pagopa.interop.common.template.action.TestChain;
import it.pagopa.interop.common.template.action.TestChainFactory;
import it.pagopa.interop.common.template.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanDelete<Request, Response, Model extends TestModel> {

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doDelete(Request request);

    default TestChain<Response, Model> delete(Request request) {
        return getChainFactory().build(() -> doDelete(request), res -> List.of());
    }
}

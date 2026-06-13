package it.pagopa.interop.common.service.template.rest;

import it.pagopa.interop.common.service.template.action.TestChain;
import it.pagopa.interop.common.service.template.action.TestChainFactory;
import it.pagopa.interop.common.domain.model.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanDelete<Request, Response, Model extends TestModel> {

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doDelete(Request request);

    default TestChain<Response, Model> delete(Request request) {
        return getChainFactory().build(() -> doDelete(request), res -> List.of());
    }
}

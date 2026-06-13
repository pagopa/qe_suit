package it.pagopa.interop.common.template.rest;

import it.pagopa.interop.common.template.action.TestChain;
import it.pagopa.interop.common.template.action.TestChainFactory;
import it.pagopa.interop.common.template.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanReadAll<Request, Response, Model extends TestModel> {

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doReadAll(Request request);

    default TestChain<Response, Model> readAll(Request request) {
        return getChainFactory().build(() -> doReadAll(request), this::updateModelsAfterRead);
    }

    List<Model> updateModelsAfterRead(Response response);
}

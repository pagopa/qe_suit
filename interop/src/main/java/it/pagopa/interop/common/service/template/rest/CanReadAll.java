package it.pagopa.interop.common.service.template.rest;

import it.pagopa.interop.common.service.template.action.TestChain;
import it.pagopa.interop.common.service.template.action.TestChainFactory;
import it.pagopa.interop.common.domain.model.TestModel;
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

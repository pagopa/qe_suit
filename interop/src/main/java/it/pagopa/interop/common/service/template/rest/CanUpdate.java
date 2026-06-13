package it.pagopa.interop.common.service.template.rest;

import it.pagopa.interop.bff.service.action.TestChain;
import it.pagopa.interop.bff.service.action.TestChainFactory;
import it.pagopa.interop.common.domain.model.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanUpdate<Request, Response, Model extends TestModel> {

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doUpdate(Request request);

    default TestChain<Response, Model> update(Request request) {
        return getChainFactory().build(() -> doUpdate(request), res -> List.of(updateModelAfterModify(res)));
    }

    Model updateModelAfterModify(Response response);
}

package it.pagopa.interop.common.service.template.rest;

import it.pagopa.interop.bff.service.action.TestChain;
import it.pagopa.interop.bff.service.action.TestChainFactory;
import it.pagopa.interop.common.domain.model.TestModel;
import it.pagopa.interop.common.service.template.RequestOverride;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanCreate<Request, Response, Model extends TestModel> {

    default TestChain<Response, Model> create() {
        return create(request -> {
        });
    }

    default TestChain<Response, Model> create(RequestOverride<Request> requestOverride) {
        Request request = doDefaultCreationRequest();
        requestOverride.applyTo(request);

        return create(request);
    }

    default TestChain<Response, Model> create(Request request){
        return getChainFactory().build(() -> doCreate(request), resp -> List.of(updateModelAfterCreate(resp)));
    }

    Request doDefaultCreationRequest();

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doCreate(Request request);

    Model updateModelAfterCreate(Response response);
}

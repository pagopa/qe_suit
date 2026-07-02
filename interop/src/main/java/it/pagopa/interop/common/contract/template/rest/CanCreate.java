package it.pagopa.interop.common.contract.template.rest;

import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.TestChainFactory;
import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.common.contract.request.RequestOverride;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanCreate<Request, Response, Model extends Identifiable> {

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

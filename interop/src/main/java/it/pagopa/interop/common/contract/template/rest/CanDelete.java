package it.pagopa.interop.common.contract.template.rest;

import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.TestChainFactory;
import it.pagopa.interop.common.contract.model.Identifiable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanDelete<Request, Response, Model extends Identifiable> {

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doDelete(Request request);

    default TestChain<Response, Model> delete(Request request) {
        return getChainFactory().build(() -> doDelete(request), res -> List.of());
    }
}

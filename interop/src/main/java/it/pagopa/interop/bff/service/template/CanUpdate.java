package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.bff.service.action.TestChain;
import it.pagopa.interop.bff.service.action.TestChainFactory;
import org.springframework.http.ResponseEntity;

public interface CanUpdate<Entity, Request> {

    TestChainFactory getChainFactory();

    ResponseEntity<Entity> doUpdate(Request request);

    default TestChain<Entity> update(Request request) {
        return getChainFactory().build(() -> doUpdate(request));
    }
}

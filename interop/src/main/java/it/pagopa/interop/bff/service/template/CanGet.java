package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.bff.service.action.TestChain;
import it.pagopa.interop.bff.service.action.TestChainFactory;
import org.springframework.http.ResponseEntity;

public interface CanGet<Entity, ID> {

    TestChainFactory getChainFactory();

    ResponseEntity<Entity> doGet(ID id);

    default TestChain<Entity> get(ID id) {
        return getChainFactory().build(() -> doGet(id));
    }
}

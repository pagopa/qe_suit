package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.bff.service.action.TestChain;
import it.pagopa.interop.bff.service.action.TestChainFactory;
import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

public interface CanCreate<Entity, Request> {

    default TestChain<Entity> create() {
        return create(request -> {
        });
    }

    default TestChain<Entity> create(Consumer<Request> requestOverride) {
        // 1. Genera il payload valido di default
        Request request = doDefaultCreationRequest();

        // 2. Applica le modifiche richieste dal test (RequestOverride)
        requestOverride.accept(request);

        // 3. Avvia la catena passando la chiamata reale dell'OpenAPI client
        return getChainFactory().build(() -> doCreate(request));
    }

    Request doDefaultCreationRequest();

    TestChainFactory getChainFactory();

    ResponseEntity<Entity> doCreate(Request request);
}

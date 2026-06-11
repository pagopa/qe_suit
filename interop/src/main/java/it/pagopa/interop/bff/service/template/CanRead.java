package it.pagopa.interop.bff.service.template;

import it.pagopa.interop.bff.service.action.TestChain;
import it.pagopa.interop.bff.service.action.TestChainFactory;
import it.pagopa.interop.common.domain.model.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CanRead<Request, Response, Model extends TestModel> {

    default TestChain<Response, Model> read(Request request) {
        return getChainFactory().build(() -> doRead(request), resp -> List.of(updateModelAfterRead(resp)));
    }

    TestChainFactory getChainFactory();

    ResponseEntity<Response> doRead(Request request);

    Model updateModelAfterRead(Response response);
}
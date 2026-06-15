package it.pagopa.interop.common.contract.template.action;

import it.pagopa.interop.common.contract.model.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Finalizer<Response, Model extends TestModel> {
    ResponseEntity<Response> getResponse();

    Model getModel();

    List<Model> getModels();
}

package it.pagopa.interop.common.service.template.action;

import it.pagopa.interop.common.domain.model.TestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Finalizer<Response, Model extends TestModel> {
    ResponseEntity<Response> getResponse();

    Model getModel();

    List<Model> getModels();
}

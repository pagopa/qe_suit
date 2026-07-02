package it.pagopa.interop.common.contract.template.action;

import it.pagopa.interop.common.contract.model.Identifiable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Finalizer<Response, Model extends Identifiable> {
    ResponseEntity<Response> getResponse();

    Model getModel();

    List<Model> getModels();
}

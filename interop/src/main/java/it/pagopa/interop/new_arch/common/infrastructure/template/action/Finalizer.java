package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Finalizer<Response, Model extends Identifiable> {
    ResponseEntity<Response> getResponse();

    Model getModel();

    List<Model> getModels();
}

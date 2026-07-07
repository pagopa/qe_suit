package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;

import java.util.List;

public interface DomainFinalizer<Model extends Identifiable> {

    Model getModel();

    List<Model> getModels();
}

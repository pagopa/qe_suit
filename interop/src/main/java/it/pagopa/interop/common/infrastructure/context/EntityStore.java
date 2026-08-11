package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.Identifiable;

import java.util.Optional;
import java.util.UUID;

public interface EntityStore {
    <Model extends Identifiable> void upsert(Model model);

    <Model extends Identifiable> Optional<Model> getById(UUID id, Class<Model> modelClass);

    <Model extends Identifiable> Optional<Model> getLast(Class<Model> modelClass);

    <Model extends Identifiable> Model getLastOrThrow(Class<Model> modelClass);
}

package it.pagopa.kernel.context;

import it.pagopa.interop.common.kernel.Identifiable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface EntityStore {
    <Model extends Identifiable> void upsert(Model model);

    <Model extends Identifiable> Optional<Model> getById(UUID id, Class<Model> modelClass);

    <Model extends Identifiable> Optional<Model> getLast(Class<Model> modelClass);

    <Model extends Identifiable> Model getLastOrThrow(Class<Model> modelClass);

    <Model extends Identifiable> Optional<Model> find(Class<Model> modelClass, Predicate<? super Model> predicate);
}

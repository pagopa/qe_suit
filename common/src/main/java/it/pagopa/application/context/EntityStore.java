package it.pagopa.application.context;

import it.pagopa.domain.Identifiable;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public interface EntityStore {
    default <Model extends Identifiable> void upsert(Model model) {
        upsert(null, model);
    }

    <Model extends Identifiable> void upsert(@Nullable String alias, Model model);

    <Model extends Identifiable> Optional<Model> getByAlias(String alias, Class<Model> modelClass);

    default <Model extends Identifiable> Model getByAliasOrThrow(String alias, Class<Model> modelClass) {
        return getByAlias(alias, modelClass).orElseThrow(() -> new IllegalArgumentException("No entity found for alias: " + alias));
    }

    <Model extends Identifiable> Optional<Model> getById(UUID id, Class<Model> modelClass);

    <Model extends Identifiable> List<Model> getAll(Class<Model> modelClass);

    <Model extends Identifiable> Optional<Model> getFirst(Class<Model> modelClass);

    <Model extends Identifiable> Model getFirstOrThrow(Class<Model> modelClass);

    <Model extends Identifiable> Optional<Model> getLast(Class<Model> modelClass);

    <Model extends Identifiable> Model getLastOrThrow(Class<Model> modelClass);

    <Model extends Identifiable> Optional<Model> find(Class<Model> modelClass, Predicate<? super Model> predicate);
}

<<<<<<<< HEAD:common/src/main/java/it/pagopa/application/context/EntityStore.java
package it.pagopa.application.context;
========
package it.pagopa.kernel.context;
>>>>>>>> b64eb6e3 (refactor: [QA-15573] migrate classi comune dal modulo interop al modulo common e refactor degli imports.):common/src/main/java/it/pagopa/kernel/context/EntityStore.java

import it.pagopa.domain.Identifiable;

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

package it.pagopa.infrastructure.context;

import it.pagopa.application.context.EntityStore;
import it.pagopa.domain.Identifiable;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class InMemoryEntityStore implements EntityStore {
    private final ThreadLocal<Map<Class<? extends Identifiable>, List<Identifiable>>> storage =
            ThreadLocal.withInitial(ConcurrentHashMap::new);
    private final ThreadLocal<Map<Class<? extends Identifiable>, Map<String, UUID>>> aliases =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    @Override
    @SuppressWarnings("unchecked")
    public <Model extends Identifiable> void upsert(@Nullable String alias, Model model) {
        Objects.requireNonNull(model, "model non può essere null");
        UUID id = Objects.requireNonNull(model.getId(), "model.id non può essere null");
        Class<Model> modelClass = (Class<Model>) model.getClass();
        if (alias != null) {
            if (alias.isBlank()) {
                throw new IllegalArgumentException("L'alias non può essere vuoto");
            }
            UUID registeredId = aliases.get()
                    .getOrDefault(modelClass, Map.of()).get(alias);
            if (registeredId != null && !registeredId.equals(id)) {
                throw new IllegalArgumentException(
                        "Alias già associato a un'altra entità di tipo "
                                + modelClass.getSimpleName() + ": " + alias
                );
            }
        }

        List<Model> entries = entries(modelClass);
        int existingIndex = indexOfById(entries, id);

        if (existingIndex >= 0) {
            entries.set(existingIndex, model);
        } else {
            entries.add(model);
        }

        if (alias != null) {
            aliases.get().computeIfAbsent(modelClass, ignored -> new ConcurrentHashMap<>())
                    .put(alias, id);
        }
    }

    @Override
    public <Model extends Identifiable> Optional<Model> getByAlias(String alias, Class<Model> modelClass) {
        UUID id = aliases.get().getOrDefault(modelClass, Map.of()).get(alias);
        return id == null ? Optional.empty() : getById(id, modelClass);
    }

    @Override
    public <Model extends Identifiable> Optional<Model> getById(UUID id, Class<Model> modelClass) {
        List<Model> entries = entries(modelClass);
        int existingIndex = indexOfById(entries, id);
        return existingIndex >= 0 ? Optional.of(entries.get(existingIndex)) : Optional.empty();
    }

    @Override
    public <Model extends Identifiable> List<Model> getAll(Class<Model> modelClass) {
        return List.copyOf(entries(modelClass));
    }

    @Override
    public <Model extends Identifiable> Optional<Model> getFirst(Class<Model> modelClass) {
        List<Model> entries = entries(modelClass);
        return entries.isEmpty() ? Optional.empty() : Optional.of(entries.get(0));
    }

    @Override
    public <Model extends Identifiable> Model getFirstOrThrow(Class<Model> modelClass) {
        return getFirst(modelClass)
                .orElseThrow(() -> new NoSuchElementException("Nessun elemento trovato per il tipo: " + modelClass.getSimpleName()));
    }

    @Override
    public <Model extends Identifiable> Optional<Model> getLast(Class<Model> modelClass) {
        List<Model> entries = entries(modelClass);
        return entries.isEmpty() ? Optional.empty() : Optional.of(entries.get(entries.size() - 1));
    }

    @Override
    public <Model extends Identifiable> Model getLastOrThrow(Class<Model> modelClass) {
        return getLast(modelClass)
                .orElseThrow(() -> new NoSuchElementException("Nessun elemento trovato per il tipo: " + modelClass.getSimpleName()));
    }

    @Override
    public <Model extends Identifiable> Optional<Model> find(Class<Model> modelClass, Predicate<? super Model> predicate) {
        List<Model> entries = entries(modelClass);
        for (Model entry : entries) {
            if (predicate.test(entry)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <Model extends Identifiable> List<Model> entries(Class<Model> modelClass) {
        return (List<Model>) storage.get().computeIfAbsent(modelClass, ignored -> new ArrayList<>());
    }

    private <Model extends Identifiable> int indexOfById(List<Model> entries, UUID id) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}

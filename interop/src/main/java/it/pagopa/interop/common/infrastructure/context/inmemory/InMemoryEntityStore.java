package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.kernel.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class InMemoryEntityStore implements EntityStore {
    private final ThreadLocal<Map<Class<? extends Identifiable>, List<Identifiable>>> storage =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    @Override
    @SuppressWarnings("unchecked")
    public <Model extends Identifiable> void upsert(Model model) {
        Class<Model> modelClass = (Class<Model>) model.getClass();
        List<Model> entries = entries(modelClass);
        int existingIndex = indexOfById(entries, model.getId());

        if (existingIndex >= 0) {
            entries.set(existingIndex, model);
            return;
        }

        entries.add(model);
    }

    @Override
    public <Model extends Identifiable> Optional<Model> getById(UUID id, Class<Model> modelClass) {
        List<Model> entries = entries(modelClass);
        int existingIndex = indexOfById(entries, id);
        return existingIndex >= 0 ? Optional.of(entries.get(existingIndex)) : Optional.empty();
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

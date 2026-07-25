package it.pagopa.interop.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.kernel.Identifiable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ScenarioScope
public class DomainContext {

    @Getter
    @RequiredArgsConstructor
    public static class ContextEntry<Model extends Identifiable> {
        private final Model item;
        private final String alias;
    }

    private final Map<Class<? extends Identifiable>, List<ContextEntry<? extends Identifiable>>> storage = new ConcurrentHashMap<>();
    private final Map<String, Identifiable> aliasStorage = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <Model extends Identifiable> void upsert(ContextEntry<Model> entry) {
        Model item = entry.getItem();
        String alias = entry.getAlias();
        Class<Model> modelClass = (Class<Model>) item.getClass();
        UUID id = item.getId();

        List<ContextEntry<Model>> entryList = entries(modelClass);
        int index = indexOfById(entryList, id);

        if (index != -1) {
            ContextEntry<Model> oldEntry = entryList.get(index);
            if (oldEntry.getAlias() != null && !oldEntry.getAlias().equals(alias)) {
                aliasStorage.remove(oldEntry.getAlias());
            }
            entryList.set(index, entry);
            log.debug("Updated entry for type {} with ID {}", modelClass.getSimpleName(), id);
        } else {
            entryList.add(entry);
            log.debug("Inserted new entry for type {} with ID {}", modelClass.getSimpleName(), id);
        }

        if (alias != null) {
            aliasStorage.put(alias, item);
        }
    }

    public <Model extends Identifiable> void upsert(Model model) {
        ContextEntry<Model> entry = new ContextEntry<>(model, null);
        upsert(entry);
    }

    public void upsert(List<ContextEntry<? extends Identifiable>> entries) {
        for (ContextEntry<? extends Identifiable> entry : entries) {
            this.upsert(entry);
        }
    }

    @SuppressWarnings("unchecked")
    public <Model extends Identifiable> Optional<Model> getByAlias(String alias, Class<Model> modelClass) {
        Identifiable item = aliasStorage.get(alias);
        if (item == null) return Optional.empty();

        if (!modelClass.isInstance(item)) {
            throw new IllegalArgumentException(String.format(
                    "L'elemento con alias '%s' è di tipo %s, ma è stato richiesto %s",
                    alias, item.getClass().getSimpleName(), modelClass.getSimpleName()
            ));
        }
        return Optional.of((Model) item);
    }

    public <Model extends Identifiable> Optional<Model> getById(UUID id, Class<Model> modelClass) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        int index = indexOfById(entryList, id);

        if (index != -1) {
            return Optional.of(entryList.get(index).getItem());
        }

        log.debug("Nessun elemento trovato per il tipo {} con ID {}", modelClass.getSimpleName(), id);
        return Optional.empty();
    }

    public <Model extends Identifiable> Model getByIdOrElseThrow(UUID id, Class<Model> modelClass) {
        var result = getById(id, modelClass);
        if (result.isEmpty())
            throw new NoSuchElementException("Nessun elemento trovato per il tipo " + modelClass.getSimpleName() + " con ID " + id);
        return result.get();
    }

    public Optional<Identifiable> getByAlias(String alias) {
        return Optional.ofNullable(aliasStorage.get(alias));
    }

    public <Model extends Identifiable> Optional<Model> getFirst(Class<Model> modelClass) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        return entryList.isEmpty() ? Optional.empty() : Optional.of(entryList.get(0).getItem());
    }

    /**
     * Recupera il primo elemento del tipo richiesto che soddisfa il predicato specificato.
     */
    public <Model extends Identifiable> Optional<Model> find(Class<Model> modelClass, java.util.function.Predicate<? super Model> predicate) {
        return entries(modelClass).stream()
                .map(ContextEntry::getItem)
                .filter(predicate)
                .findFirst();
    }

    public <Model extends Identifiable> Optional<Model> getLast(Class<Model> modelClass) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        return entryList.isEmpty() ? Optional.empty() : Optional.of(entryList.get(entryList.size() - 1).getItem());
    }

    public <Model extends Identifiable> Model getLastOrThrow(Class<Model> modelClass) {
        return getLast(modelClass)
                .orElseThrow(() -> new NoSuchElementException("Nessun elemento trovato per il tipo: " + modelClass.getSimpleName()));
    }

    public <Model extends Identifiable> Optional<Model> getAtIndex(Class<Model> modelClass, int index) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        if (index < 0 || index >= entryList.size()) {
            log.warn("Richiesto indice {} fuori dai limiti per {}. Size: {}", index, modelClass.getSimpleName(), entryList.size());
            return Optional.empty();
        }
        return Optional.of(entryList.get(index).getItem());
    }

    private <Model extends Identifiable> int indexOfById(List<ContextEntry<Model>> entries, UUID id) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getItem().getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private <Model extends Identifiable> List<ContextEntry<Model>> entries(Class<Model> modelClass) {
        return (List<ContextEntry<Model>>) (List<?>) storage.computeIfAbsent(modelClass, k -> new ArrayList<>());
    }
}
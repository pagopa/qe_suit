package it.pagopa.interop.common.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.domain.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ScenarioScope
public class ScenarioContext {

    private final Map<Class<? extends TestModel>, List<ContextEntry<? extends TestModel>>> storage = new ConcurrentHashMap<>();
    private final Map<String, TestModel> aliasStorage = new ConcurrentHashMap<>();

    // Riferimento all'ultima risposta HTTP ricevuta nello scenario corrente
    private ResponseEntity<?> lastResponseEntity;

    /**
     * Memorizza l'ultima ResponseEntity ricevuta da una chiamata API.
     */
    public void setLastResponseEntity(ResponseEntity<?> responseEntity) {
        this.lastResponseEntity = responseEntity;
        if (responseEntity != null) {
            log.debug("Saved last ResponseEntity with status: {}", responseEntity.getStatusCode());
        }
    }

    /**
     * Recupera il codice di stato HTTP dell'ultima risposta (es. 200, 404, 500).
     */
    public Optional<Integer> getLastResponseStatusCode() {
        return Optional.ofNullable(lastResponseEntity)
                .map(response -> response.getStatusCode().value());
    }

    /**
     * Recupera il body dell'ultima risposta come Object generico.
     */
    public Optional<Object> getLastResponseBody() {
        return Optional.ofNullable(lastResponseEntity)
                .map(ResponseEntity::getBody);
    }

    /**
     * Recupera il body dell'ultima risposta castato automaticamente al tipo atteso.
     * Ritorna Optional.empty() se il body è nullo o non è compatibile con la classe richiesta.
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getLastResponseBody(Class<T> targetClass) {
        return getLastResponseBody()
                .filter(targetClass::isInstance)
                .map(body -> (T) body);
    }

    @SuppressWarnings("unchecked")
    public <Model extends TestModel> void upsert(ContextEntry<Model> entry) {
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

    public <Model extends TestModel> void upsert(Model model){
        ContextEntry<Model> entry = new ContextEntry<>(model, null);
        upsert(entry);
    }

    public void upsert(List<ContextEntry<? extends TestModel>> entries) {
        for (ContextEntry<? extends TestModel> entry : entries) {
            this.upsert(entry);
        }
    }

    @SuppressWarnings("unchecked")
    public <Model extends TestModel> Optional<Model> getByAlias(String alias, Class<Model> modelClass) {
        TestModel item = aliasStorage.get(alias);
        if (item == null) return Optional.empty();

        if (!modelClass.isInstance(item)) {
            throw new IllegalArgumentException(String.format(
                    "L'elemento con alias '%s' è di tipo %s, ma è stato richiesto %s",
                    alias, item.getClass().getSimpleName(), modelClass.getSimpleName()
            ));
        }
        return Optional.of((Model) item);
    }

    public Optional<TestModel> getByAlias(String alias) {
        return Optional.ofNullable(aliasStorage.get(alias));
    }

    public <Model extends TestModel> Optional<Model> getFirst(Class<Model> modelClass) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        return entryList.isEmpty() ? Optional.empty() : Optional.of(entryList.get(0).getItem());
    }

    public <Model extends TestModel> Optional<Model> getLast(Class<Model> modelClass) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        return entryList.isEmpty() ? Optional.empty() : Optional.of(entryList.get(entryList.size() - 1).getItem());
    }

    public <Model extends TestModel> Model getLastOrThrow(Class<Model> modelClass){
        return getLast(modelClass)
                .orElseThrow(() -> new NoSuchElementException("Nessun elemento trovato per il tipo: " + modelClass.getSimpleName()));
    }

    public <Model extends TestModel> Optional<Model> getAtIndex(Class<Model> modelClass, int index) {
        List<ContextEntry<Model>> entryList = entries(modelClass);
        if (index < 0 || index >= entryList.size()) {
            log.warn("Richiesto indice {} fuori dai limiti per {}. Size: {}", index, modelClass.getSimpleName(), entryList.size());
            return Optional.empty();
        }
        return Optional.of(entryList.get(index).getItem());
    }

    private <Model extends TestModel> int indexOfById(List<ContextEntry<Model>> entries, UUID id) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getItem().getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private <Model extends TestModel> List<ContextEntry<Model>> entries(Class<Model> modelClass) {
        return (List<ContextEntry<Model>>) (List<?>) storage.computeIfAbsent(modelClass, k -> new ArrayList<>());
    }
}
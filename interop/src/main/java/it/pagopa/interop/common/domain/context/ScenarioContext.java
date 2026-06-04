package it.pagopa.interop.common.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.domain.model.TestChildModel;
import it.pagopa.interop.common.domain.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Component
@Slf4j
@ScenarioScope
public class ScenarioContext {

    private final Map<Class<?>, List<Object>> storage = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public void upsert(Object item) {
        if (item == null) return;

        // CASO 1: È un sotto-modello di dominio -> Merge nel padre
        if (item instanceof TestChildModel<?> child) {
            Class<? extends TestModel> parentClass = child.getParentClass();
            TestModel lastParent = getLast(parentClass);

            if (lastParent != null) {
                ((TestChildModel<TestModel>) child).mergeInto(lastParent);
                return;
            }
            throw new IllegalStateException("Impossibile accoppiare il sotto-modello " + item.getClass().getSimpleName());
        }

        Class<?> clazz = item.getClass();
        List<Object> items = storage.computeIfAbsent(clazz, k -> new ArrayList<>());

        // CASO 2: È un modello di dominio principale -> Gestiamo l'Update basato su ID
        if (item instanceof TestModel testModel) {
            int index = indexOfById(items, testModel.getId());
            if (index >= 0) {
                items.set(index, testModel);
            } else {
                items.add(testModel);
            }
        } else {
            // CASO 3: FALLBACK per DTO grezzi (CreatedResource, Problem, ecc.)
            // Non avendo un ID di dominio, lo inseriamo semplicemente in coda come "storico"
            log.warn("Upsert di un oggetto non TestModel senza ID: {}. Verrà aggiunto in coda senza merge.", item.getClass().getSimpleName());
            items.add(item);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> clazz) {
        return (List<T>) storage.getOrDefault(clazz, List.of());
    }

    public <T> T getLast(Class<T> clazz) {
        List<T> list = getAll(clazz);
        if (list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }

    public <T> T getFirst(Class<T> clazz) {
        List<T> list = getAll(clazz);
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public <T> Optional<T> find(Class<T> clazz, Predicate<T> predicate) {
        return getAll(clazz).stream().filter(predicate).findFirst();
    }

    public void clear() {
        storage.clear();
    }

    private int indexOfById(List<Object> items, Object id) {
        if (id == null) return -1;
        for (int i = 0; i < items.size(); i++) {
            Object current = items.get(i);
            if (current instanceof TestModel tm && id.equals(tm.getId())) {
                return i;
            }
        }
        return -1;
    }
}
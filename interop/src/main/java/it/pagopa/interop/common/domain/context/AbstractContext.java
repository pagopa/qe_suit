package it.pagopa.interop.common.domain.context;

import it.pagopa.interop.common.domain.model.AbstractModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractContext<Model extends AbstractModel> {

    private final List<Model> items = new ArrayList<>();

    /**
     * Fusione tra UPdate e inSERT. Aggiunge il model se non esiste, altrimenti sostituisce quello con stesso uniqueIdentifier.
     */
    public void upsert(Model item) {
        String id = item.getUniqueIdentifier();
        int index = indexOfById(id);
        if (index >= 0) {
            items.set(index, item);
        } else {
            items.add(item);
        }
    }

    public List<Model> getAll() {
        return List.copyOf(items);
    }

    public Model getFirst() {
        if (items.isEmpty()) throw new IllegalStateException("No items in context");
        return items.get(0);
    }

    public Model getLast() {
        if (items.isEmpty()) throw new IllegalStateException("No items in context");
        return items.get(items.size() - 1);
    }

    public Model getByIndex(int index) {
        return items.get(index);
    }

    public Optional<Model> findFirst(Predicate<Model> predicate) {
        return items.stream().filter(predicate).findFirst();
    }

    public Model getFirstOrThrow(Predicate<Model> predicate) {
        return findFirst(predicate)
                .orElseThrow(() -> new IllegalStateException("No item matching predicate in context"));
    }

    public Optional<Model> findById(String uniqueIdentifier) {
        return items.stream()
                .filter(i -> i.getUniqueIdentifier().equals(uniqueIdentifier))
                .findFirst();
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    private int indexOfById(String uniqueIdentifier) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUniqueIdentifier().equals(uniqueIdentifier)) {
                return i;
            }
        }
        return -1;
    }
}
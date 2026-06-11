package it.pagopa.interop.common.utils;

import it.pagopa.interop.common.domain.model.TestModel;

import java.util.*;

public final class DependencyResolver {

    // Lista immutabile delle dipendenze disponibili per lo step corrente
    private final List<TestModel> dependencies;

    private DependencyResolver(TestModel... dependencies) {
        if (dependencies == null || dependencies.length == 0) {
            this.dependencies = List.of();
        } else {
            // Convertiamo in lista e invertiamo l'ordine: l'ultimo modello inserito
            // nel contesto diventa il primo a essere valutato (Latest-wins).
            List<TestModel> reversedList = new ArrayList<>(Arrays.asList(dependencies));
            Collections.reverse(reversedList);
            this.dependencies = List.copyOf(reversedList);
        }
    }

    /**
     * Factory method per istanziare il resolver a partire dalle dipendenze dello step.
     */
    public static DependencyResolver of(TestModel... dependencies) {
        return new DependencyResolver(dependencies);
    }

    /**
     * Tenta di recuperare l'istanza più recente del modello richiesto.
     */
    @SuppressWarnings("unchecked")
    public <T extends TestModel> Optional<T> find(Class<T> clazz) {
        return dependencies.stream()
                .filter(clazz::isInstance)
                .map(obj -> (T) obj)
                .findFirst();
    }

    /**
     * Recupera il modello richiesto o lancia un'eccezione descrittiva se assente.
     * Utile per i campi obbligatori della Request che non hanno fallback possibili.
     */
    public <T extends TestModel> T get(Class<T> clazz) {
        return find(clazz)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Errore di contesto: Manca la dipendenza obbligatoria [%s] richiesta per questo step.",
                                clazz.getSimpleName())
                ));
    }
}

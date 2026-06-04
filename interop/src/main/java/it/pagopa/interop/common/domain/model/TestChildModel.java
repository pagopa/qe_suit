package it.pagopa.interop.common.domain.model;

public interface TestChildModel<Parent extends TestModel> extends TestModel {

    /**
     * Ritorna la classe dell'Aggregate Root (il padre) a cui questo figlio appartiene.
     */
    Class<Parent> getParentClass();

    /**
     * Definisce la logica con cui il figlio si aggiorna, si inserisce o si sostituisce dentro il padre.
     */
    void mergeInto(Parent parent);
}

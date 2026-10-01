package it.pagopa.send.common.journey.application;

import it.pagopa.domain.Identifiable;

/**
 * Recupera l'ultima entità creata durante lo scenario/test corrente da {@code EntityStore}, sul
 * modello di {@code FinalizerJourney} in interop. Non è parametrizzato su {@code SELF} come gli
 * altri {@link JourneyModule}: {@code get(...)} è una chiamata terminale, non prosegue la catena
 * fluente.
 */
public interface FinalizerJourney extends JourneyModule {
    <T extends Identifiable> T get(Class<T> clazz);
}

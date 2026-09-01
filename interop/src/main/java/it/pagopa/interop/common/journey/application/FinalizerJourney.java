package it.pagopa.interop.common.journey.application;

import it.pagopa.kernel.domain.Identifiable;

public interface FinalizerJourney extends JourneyModule{
    <T extends Identifiable> T get(Class<T> clazz);
}

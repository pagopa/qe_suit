package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.FinalizerJourney;
import it.pagopa.domain.Identifiable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizerJourneyImpl implements FinalizerJourney {

    private final EntityStore entityStore;

    @Override
    public <T extends Identifiable> T get(Class<T> clazz) {
        return entityStore.getLastOrThrow(clazz);
    }
}

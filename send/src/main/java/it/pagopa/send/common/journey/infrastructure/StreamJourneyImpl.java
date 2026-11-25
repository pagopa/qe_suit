package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.journey.application.StreamJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.stream.application.StreamUseCase;
import it.pagopa.send.common.stream.domain.EventStream;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.stream.infrastructure.StreamCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamRequestFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Composer fluente per gli stream pn-stream, sul modello di {@link DelegationJourneyImpl}: bean
 * singleton senza stato proprio, lo stream creato resta in {@code EntityStore}.
 * <p>
 * Le chiamate pn-stream usano l'api-key dell'utenza in sessione, percio' qui si imposta l'owner
 * indicato; nei contract test il framework ripristina poi l'identita' dichiarata con {@code as(...)}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamJourneyImpl implements StreamJourney<StreamJourneyImpl> {

    private final StreamUseCase streamUseCase;
    private final StreamRequestFactory requestFactory;
    private final StreamCreatedTracker createdTracker;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;

    @Override
    public StreamJourneyImpl withFreshStream(User owner) {
        purgeTestStreams(owner);
        EventStream stream = streamUseCase.createStream(requestFactory.testStreamTitle());
        entityStore.upsert(stream);
        log.info("Stream {} creato per {}", stream.streamId(), owner.getUsername());
        return this;
    }

    @Override
    public void purgeTestStreams(User owner) {
        currentUserSession.setUser(owner);
        streamUseCase.removeStreams(this::isTestStream)
                .forEach(removed -> createdTracker.forget(removed.streamId()));
    }

    private boolean isTestStream(EventStream stream) {
        return createdTracker.isTracked(stream.streamId())
                || (stream.title() != null && stream.title().startsWith(StreamRequestFactory.TEST_STREAM_PREFIX));
    }
}

package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.send.common.journey.application.StreamLegacyJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.stream.application.StreamLegacyUseCase;
import it.pagopa.send.common.stream.domain.LegacyEventStream;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Composer fluente per gli stream pn-stream legacy, sul modello di {@link DelegationJourneyImpl}: bean
 * singleton senza stato proprio. Creazione, registrazione per la pulizia e {@code EntityStore} sono
 * responsabilita' del gateway.
 * <p>
 * Le chiamate pn-stream usano l'api-key dell'utenza in sessione, percio' qui si imposta l'owner
 * indicato; nei contract test il framework ripristina poi l'identita' dichiarata con {@code as(...)}.
 */
@Component
@RequiredArgsConstructor
public class StreamLegacyJourneyImpl implements StreamLegacyJourney<StreamLegacyJourneyImpl> {

    private final StreamLegacyUseCase streamLegacyUseCase;
    private final CurrentUserSession currentUserSession;

    @Override
    public StreamLegacyJourneyImpl withFreshLegacyStream(User owner, StreamApiVersion version) {
        create(owner, version);
        return this;
    }

    @Override
    public StreamLegacyJourneyImpl withDisabledLegacyStream(User owner, StreamApiVersion version) {
        if (!version.supportsDisable()) {
            throw new IllegalArgumentException("Le API pn-stream " + version + " non prevedono la disabilitazione");
        }
        LegacyEventStream stream = create(owner, version);
        streamLegacyUseCase.disableStream(version, stream.streamId());
        return this;
    }

    @Override
    public StreamLegacyJourneyImpl withRemovedLegacyStream(User owner, StreamApiVersion version) {
        LegacyEventStream stream = create(owner, version);
        streamLegacyUseCase.removeStream(version, stream.streamId());
        return this;
    }

    @Override
    public void removeCreatedLegacyStreams(User owner) {
        currentUserSession.setUser(owner);
        streamLegacyUseCase.removeCreatedStreams();
    }

    @Override
    public void removeLeftoverLegacyTestStreams(User owner) {
        currentUserSession.setUser(owner);
        streamLegacyUseCase.removeLeftoverTestStreams();
    }

    private LegacyEventStream create(User owner, StreamApiVersion version) {
        removeCreatedLegacyStreams(owner);
        return streamLegacyUseCase.createStream(version);
    }
}

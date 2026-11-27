package it.pagopa.send.stream.infrastructure;

import it.pagopa.send.generated.openapi.clients.stream.model.CommunicationType;
import it.pagopa.send.generated.openapi.clients.stream.model.StreamCreationRequestV30;
import it.pagopa.send.generated.openapi.clients.stream.model.StreamRequestV30;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Richieste baseline delle operazioni di pn-stream v3.0, valide per contratto: un fallimento del
 * contract test deve essere sempre attribuibile alla mutazione applicata dal fuzzer, non al fixture.
 * <p>
 * Tutti gli stream creati dai test hanno titolo con prefisso {@link #TEST_STREAM_PREFIX}, cosi' da
 * poterli riconoscere e rimuovere (limite di 10 stream attivi per PA).
 */
@Component
public class StreamRequestFactory {

    public static final String TEST_STREAM_PREFIX = "QE-CONTRACT-";

    /** Il flag non e' modificabile in update: creazione e update devono usare lo stesso valore. */
    static final boolean WAIT_FOR_ACCEPTED = true;

    public String testStreamTitle() {
        return TEST_STREAM_PREFIX + UUID.randomUUID();
    }

    /**
     * Stream TIMELINE senza gruppi (consumabile da api-key senza gruppi) con il solo placeholder
     * {@code DEFAULT}. {@code replacedStreamId} e' volutamente assente: valorizzarlo disabiliterebbe
     * uno stream esistente.
     */
    public StreamCreationRequestV30 creationRequest() {
        return new StreamCreationRequestV30()
                .title(testStreamTitle())
                .eventType(StreamCreationRequestV30.EventTypeEnum.TIMELINE)
                .groups(new ArrayList<>())
                .filterValues(new ArrayList<>(List.of("DEFAULT")))
                .waitForAccepted(WAIT_FOR_ACCEPTED)
                .communicationType(CommunicationType.LEGAL);
    }

    /**
     * Update compatibile con lo stream creato da {@link #creationRequest()}: stessi gruppi (vuoti),
     * stesso {@code waitForAccepted}, filtro esteso con {@code SEND_COURTESY_MESSAGE}.
     */
    public StreamRequestV30 updateRequest() {
        return new StreamRequestV30()
                .title(testStreamTitle())
                .eventType(StreamRequestV30.EventTypeEnum.TIMELINE)
                .groups(new ArrayList<>())
                .filterValues(new ArrayList<>(List.of("DEFAULT", "SEND_COURTESY_MESSAGE")))
                .waitForAccepted(WAIT_FOR_ACCEPTED);
    }
}

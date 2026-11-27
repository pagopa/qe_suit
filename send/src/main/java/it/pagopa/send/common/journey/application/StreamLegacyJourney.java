package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.User;

/**
 * Precondizioni sugli stream pn-stream creati con le API legacy (v1.0, v2.3-v2.9), una per ogni
 * stato della macchina a stati dello {@code streamId}:
 * <pre>
 * (nessuno) --create--&gt; ATTIVO --disable (solo v2.x)--&gt; DISABILITATO
 *                       ATTIVO / DISABILITATO --remove--&gt; RIMOSSO
 * </pre>
 * Lo stream e' creato con la versione indicata, perche' il backend rifiuta l'accesso da una
 * versione diversa, e resta in {@code EntityStore} ({@code get(LegacyEventStream.class)}). Prima di
 * ogni creazione si rimuovono gli stream creati in precedenza da questa esecuzione (limite di stream
 * attivi per PA).
 */
public interface StreamLegacyJourney<SELF extends StreamLegacyJourney<SELF>> extends JourneyModule {

    /**
     * Stream ATTIVO.
     */
    SELF withFreshLegacyStream(User owner, StreamApiVersion version);

    /**
     * Stream DISABILITATO; non disponibile in v1.0.
     */
    SELF withDisabledLegacyStream(User owner, StreamApiVersion version);

    /**
     * Stream RIMOSSO: lo {@code streamId} non esiste piu'.
     */
    SELF withRemovedLegacyStream(User owner, StreamApiVersion version);

    /**
     * Rimuove gli stream legacy creati da questa esecuzione, ciascuno con la propria versione.
     */
    void removeCreatedLegacyStreams(User owner);

    /**
     * Rimuove gli stream legacy di test residui di esecuzioni precedenti, abbastanza vecchi da non
     * appartenere a un'esecuzione in corso sulla stessa PA.
     */
    void removeLeftoverLegacyTestStreams(User owner);
}

package it.pagopa.send.stream.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Richieste baseline delle API legacy di pn-stream (v1.0, v2.3-v2.9), valide per contratto in ogni
 * versione: un fallimento del contract test deve essere attribuibile alla mutazione del fuzzer.
 * <p>
 * Gli schemi delle versioni differiscono solo per i campi presenti ({@code groups} da v2.3,
 * {@code waitForAccepted} da v2.7): la richiesta e' costruita una volta e convertita nel modello
 * generato della versione richiesta.
 * <p>
 * Il titolo degli stream di test contiene la versione e l'istante di creazione
 * ({@code QE-LEGACY-V23-<epochMillis>-<uuid>}): la pulizia all'avvio rimuove ogni residuo con la sua
 * versione, e solo se abbastanza vecchio da non appartenere a un'esecuzione in corso. Il prefisso e'
 * diverso da quello degli stream di test v3.0 ({@code QE-CONTRACT-}, QA-19146): la purge v3.0
 * fallirebbe (403) su uno stream legacy.
 */
@Component
public class StreamLegacyRequestFactory {

    public static final String TEST_STREAM_PREFIX = "QE-LEGACY-";

    /**
     * Cursore sintatticamente valido ({@code ^0\d{37}$}) per {@code consumeEventStream}.
     */
    public static final String BASELINE_LAST_EVENT_ID = "0".repeat(38);

    /**
     * Converte la richiesta (mappa di valori semplici) nel modello generato della versione: gli enum
     * generati si deserializzano dal loro valore tramite {@code @JsonCreator}.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Eta' minima di uno stream di test perche' la pulizia all'avvio lo consideri un residuo: un'esecuzione
     * completa dura circa 15 minuti, uno stream piu' recente puo' appartenere a un'esecuzione in corso.
     */
    static final Duration LEFTOVER_MIN_AGE = Duration.ofHours(2);

    public String testStreamTitle(StreamApiVersion version) {
        return TEST_STREAM_PREFIX + version.name() + "-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID();
    }

    /**
     * Versione di uno stream di test residuo di un'esecuzione precedente: titolo di test creato da almeno
     * {@link #LEFTOVER_MIN_AGE}. Vuota per gli altri stream, compresi gli stream di test piu' recenti.
     */
    public Optional<StreamApiVersion> leftoverVersion(String title, Instant now) {
        if (title == null || !title.startsWith(TEST_STREAM_PREFIX)) {
            return Optional.empty();
        }
        String[] parts = title.substring(TEST_STREAM_PREFIX.length()).split("-", 3);
        if (parts.length < 3) {
            return Optional.empty();
        }
        Optional<Instant> createdAt = epochMillis(parts[1]);
        if (createdAt.isEmpty() || createdAt.get().isAfter(now.minus(LEFTOVER_MIN_AGE))) {
            return Optional.empty();
        }
        return Arrays.stream(StreamApiVersion.values())
                .filter(version -> version.name().equals(parts[0]))
                .findFirst();
    }

    private static Optional<Instant> epochMillis(String value) {
        try {
            return Optional.of(Instant.ofEpochMilli(Long.parseLong(value)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Stream TIMELINE senza gruppi (consumabile da api-key senza gruppi) con il solo filtro
     * {@code DEFAULT}; {@code replacedStreamId} assente: valorizzarlo disabiliterebbe un altro stream.
     */
    public <T> T creationRequest(StreamApiVersion version, Class<T> type) {
        return MAPPER.convertValue(request(version, List.of("DEFAULT")), type);
    }

    /**
     * Update compatibile con {@link #creationRequest}: stessi gruppi (vuoti), filtro esteso con
     * {@code SEND_COURTESY_MESSAGE}. In v1.0 lo schema di update coincide con quello di creazione.
     */
    public <T> T updateRequest(StreamApiVersion version, Class<T> type) {
        return MAPPER.convertValue(request(version, List.of("DEFAULT", "SEND_COURTESY_MESSAGE")), type);
    }

    private Map<String, Object> request(StreamApiVersion version, List<String> filterValues) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("title", testStreamTitle(version));
        request.put("eventType", "TIMELINE");
        if (version.supportsGroups()) {
            request.put("groups", List.of());
        }
        request.put("filterValues", filterValues);
        if (version.supportsWaitForAccepted()) {
            request.put("waitForAccepted", Boolean.TRUE);
        }
        return request;
    }
}

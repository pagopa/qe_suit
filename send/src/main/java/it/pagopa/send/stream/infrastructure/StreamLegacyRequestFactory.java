package it.pagopa.send.stream.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import org.springframework.stereotype.Component;

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
 * Il titolo degli stream di test contiene la versione ({@code QE-LEGACY-V23-<uuid>}), cosi' da
 * poterli rimuovere con la versione giusta. Il prefisso e' diverso da quello degli stream di test
 * v3.0 ({@code QE-CONTRACT-}, QA-19146): la purge v3.0 fallirebbe (403) su uno stream legacy.
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

    public String testStreamTitle(StreamApiVersion version) {
        return TEST_STREAM_PREFIX + version.name() + "-" + UUID.randomUUID();
    }

    /**
     * Versione di uno stream di test riconosciuta dal titolo, vuota per gli altri stream.
     */
    public Optional<StreamApiVersion> versionOfTestTitle(String title) {
        if (title == null || !title.startsWith(TEST_STREAM_PREFIX)) {
            return Optional.empty();
        }
        String rest = title.substring(TEST_STREAM_PREFIX.length());
        int separator = rest.indexOf('-');
        if (separator < 0) {
            return Optional.empty();
        }
        String name = rest.substring(0, separator);
        for (StreamApiVersion version : StreamApiVersion.values()) {
            if (version.name().equals(name)) {
                return Optional.of(version);
            }
        }
        return Optional.empty();
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

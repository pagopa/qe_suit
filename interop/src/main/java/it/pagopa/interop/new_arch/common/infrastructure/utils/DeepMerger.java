package it.pagopa.interop.new_arch.common.infrastructure.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.CucumberConfig;


import java.util.Map;
import java.util.Set;

public final class DeepMerger {

    // Configurazione avanzata: forziamo Jackson a ignorare i @JsonInclude(NON_NULL) dei model generati
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                @Override
                public JsonInclude.Value findPropertyInclusion(Annotated a) {
                    // Sovrascrive qualsiasi configurazione di esclusione dei null, includendo SEMPRE tutto nella mappa
                    return JsonInclude.Value.construct(JsonInclude.Include.ALWAYS, JsonInclude.Include.ALWAYS);
                }
            });

    private DeepMerger() {
    }

    public static <T> T merge(T overrideSource, T defaultTarget) {
        if (overrideSource == null) return defaultTarget;
        if (defaultTarget == null) return overrideSource;

        try {
            Map<String, Object> defaultMap = MAPPER.convertValue(defaultTarget, new TypeReference<>() {});
            Map<String, Object> overrideMap = MAPPER.convertValue(overrideSource, new TypeReference<>() {});

            Set<String> gherkinKeys = CucumberConfig.getCurrentGherkinKeys();

            deepMergeMaps(overrideMap, defaultMap, gherkinKeys);

            return (T) MAPPER.convertValue(defaultMap, defaultTarget.getClass());
        } catch (Exception e) {
            throw new IllegalStateException("Errore durante il deep merge dei modelli", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void deepMergeMaps(Map<String, Object> source, Map<String, Object> target, Set<String> gherkinKeys) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object sourceValue = entry.getValue();
            Object targetValue = target.get(key);

            // Se è un nodo contenitore/strutturale presente in entrambe le mappe, scendiamo in ricorsione
            if (sourceValue instanceof Map && targetValue instanceof Map) {
                deepMergeMaps((Map<String, Object>) sourceValue, (Map<String, Object>) targetValue, gherkinKeys);
            } else {
                // Ora che i null non vengono più droppati, "personalData" entrerà finalmente qui.
                // Se la colonna era presente in Gherkin, il null dell'override vince e sovrascrive il false di default.
                if (gherkinKeys != null && gherkinKeys.contains(key)) {
                    target.put(key, sourceValue);
                }
            }
        }
    }
}
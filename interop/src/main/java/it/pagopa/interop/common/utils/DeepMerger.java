package it.pagopa.interop.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.interop.common.config.CucumberConfig;

import java.util.Map;
import java.util.Set;

public final class DeepMerger {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private DeepMerger() {
    }

    public static <T> T merge(T overrideSource, T defaultTarget) {
        if (overrideSource == null) return defaultTarget;
        if (defaultTarget == null) return overrideSource;

        try {
            Map<String, Object> defaultMap = MAPPER.convertValue(defaultTarget, new TypeReference<>() {
            });
            Map<String, Object> overrideMap = MAPPER.convertValue(overrideSource, new TypeReference<>() {
            });

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

            // STRATEGIA: Se è un nodo contenitore/strutturale (es. "eservice") presente in entrambe le mappe,
            // dobbiamo SEMPRE scendere in ricorsione per esplorarlo, ignorando il filtro a questo livello.
            if (sourceValue instanceof Map && targetValue instanceof Map) {
                deepMergeMaps((Map<String, Object>) sourceValue, (Map<String, Object>) targetValue, gherkinKeys);
            } else {
                // Siamo su un campo foglia reale (es. "name", "mode", "description").
                // Applichiamo la tua regola d'oro: vince il seed solo se la colonna era presente in Gherkin
                if (gherkinKeys != null && (gherkinKeys.contains(key) || gherkinKeys.contains(entry.getKey()))) {
                    target.put(key, sourceValue);
                }
                // Se la chiave non è nel file .feature, non facciamo nulla (vince il default)
            }
        }
    }
}
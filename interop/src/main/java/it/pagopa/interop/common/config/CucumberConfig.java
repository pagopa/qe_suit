package it.pagopa.interop.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableEntryTransformer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CucumberConfig {

    // Il ThreadLocal custodisce le chiavi esplicite del test corrente
    private static final ThreadLocal<Set<String>> CURRENT_GHERKIN_KEYS = new ThreadLocal<>();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

    @DefaultDataTableEntryTransformer
    public Object transform(Map<String, String> entry, Type toValueType) {
        Map<String, String> modifiableEntry = new HashMap<>(entry);

        // Salviamo le chiavi VALIDE scritte nell'hinterland del file .feature
        CURRENT_GHERKIN_KEYS.set(entry.keySet());

        modifiableEntry.replaceAll((key, value) -> "$blank()".equals(value) ? "" : value);

        return objectMapper.convertValue(modifiableEntry, objectMapper.constructType(toValueType));
    }

    public static Set<String> getCurrentGherkinKeys() {
        return CURRENT_GHERKIN_KEYS.get();
    }

    public static void clearGherkinKeys() {
        CURRENT_GHERKIN_KEYS.remove();
    }
}
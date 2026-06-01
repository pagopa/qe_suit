package it.pagopa.interop.common.config;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableEntryTransformer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CucumberConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DefaultDataTableEntryTransformer
    public Object transform(Map<String, String> entry, Type toValueType) {
        // 1. Creiamo una copia mutabile della mappa (Cucumber potrebbe passarla come immutabile)
        Map<String, String> modifiableEntry = new HashMap<>(entry);

        //TODO: da sostituire con l'integrazione di uno strategy
        // 2. Se il valore è esattamente "$blank()", lo trasformiamo in una vera stringa vuota ""
        modifiableEntry.replaceAll((key, value) -> "$blank()".equals(value) ? "" : value);

        // 3. Jackson fa lo zapping finale sull'oggetto
        return objectMapper.convertValue(modifiableEntry, objectMapper.constructType(toValueType));
    }
}
package it.pagopa.interop.config;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableEntryTransformer;

import java.lang.reflect.Type;
import java.util.Map;

public class CucumberConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Questo trasforma AUTOMATICAMENTE qualsiasi tabella nel rispettivo Bean usando la reflection
    @DefaultDataTableEntryTransformer
    public Object transform(Map<String, String> entry, Type toValueType) {
        return objectMapper.convertValue(entry, objectMapper.constructType(toValueType));
    }
}

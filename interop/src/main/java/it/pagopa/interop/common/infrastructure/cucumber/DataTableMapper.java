package it.pagopa.interop.common.infrastructure.cucumber;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableEntryTransformer;

import it.pagopa.interop.common.infrastructure.cucumber.resolver.DataTableExpressionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataTableMapper {

    private static final ThreadLocal<Set<String>> CURRENT_GHERKIN_KEYS = new ThreadLocal<>();
    private final DataTableExpressionResolver expressionResolver;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

    @DefaultDataTableEntryTransformer
    public Object transform(Map<String, String> entry, Type toValueType) {
        Map<String, String> modifiableEntry = new HashMap<>(entry);
        CURRENT_GHERKIN_KEYS.set(entry.keySet());
        modifiableEntry.replaceAll((key, value) -> expressionResolver.resolve(value));

        return objectMapper.convertValue(modifiableEntry, objectMapper.constructType(toValueType));
    }

    public static Set<String> getCurrentGherkinKeys() {
        return CURRENT_GHERKIN_KEYS.get();
    }

    public static void clearGherkinKeys() {
        CURRENT_GHERKIN_KEYS.remove();
    }
}

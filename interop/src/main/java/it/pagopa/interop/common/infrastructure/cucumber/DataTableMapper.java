package it.pagopa.interop.common.infrastructure.cucumber;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceModeDeserializer;
import it.pagopa.interop.common.infrastructure.cucumber.dto.ProducerDeserializer;
import it.pagopa.interop.common.kernel.domain.Tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataTableMapper {

    private static final ThreadLocal<Set<String>> CURRENT_GHERKIN_KEYS = new ThreadLocal<>();

    private final Validator validator;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .registerModule(new SimpleModule()
                    .addDeserializer(Tenant.class, new ProducerDeserializer())
                    .addDeserializer(EServiceMode.class, new EServiceModeDeserializer()));

    @DefaultDataTableEntryTransformer
    public Object transform(Map<String, String> entry, Type toValueType) {
        Map<String, String> modifiableEntry = new HashMap<>(entry);
        CURRENT_GHERKIN_KEYS.set(entry.keySet());

        Object dto = objectMapper.convertValue(modifiableEntry, objectMapper.constructType(toValueType));
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return dto;
    }

    public static Set<String> getCurrentGherkinKeys() {
        return CURRENT_GHERKIN_KEYS.get();
    }

    public static void clearGherkinKeys() {
        CURRENT_GHERKIN_KEYS.remove();
    }
}

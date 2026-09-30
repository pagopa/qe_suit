package it.pagopa.infrastructure.http.restassured.contract;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.filter.Filter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContractValidationFilterFactoryTest {

    @Test
    void create_returnsOpenApiFilter_withBasePathDerivedFromBaseUrl() {
        URL spec = ContractValidationFilterFactoryTest.class.getResource("/openapi/minimal-api.yaml");
        assertNotNull(spec);

        Filter filter = ContractValidationFilterFactory.create(
                "https://example.org/base/v1",
                spec.toExternalForm()
        );

        assertInstanceOf(OpenApiValidationFilter.class, filter);
        assertEquals("/base/v1", extractApiPrefix((OpenApiValidationFilter) filter));
    }

    private static String extractApiPrefix(OpenApiValidationFilter filter) {
        Object validator = readField(filter, "validator");
        Object operationResolver = readField(validator, "apiOperationResolver");
        return (String) readField(operationResolver, "apiPrefix");
    }

    private static Object readField(Object target, String fieldName) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot read field " + fieldName, e);
            }
        }
        throw new IllegalStateException("Field not found: " + fieldName);
    }
}

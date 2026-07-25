package it.pagopa.interop.common.infrastructure.http.contract.engine;

import io.restassured.builder.RequestSpecBuilder;

import java.lang.reflect.Field;
import java.util.Map;

final class OpenApiRequestInjector {

    private static final String REQUEST_SPEC_FIELD = "reqSpec";

    void injectRawInputs(Object openApiOperation, Map<String, Object> rawInputs, String httpMethod) {
        RequestSpecBuilder builder = extractRequestSpecBuilder(openApiOperation);

        if (hasQueryParams(httpMethod)) {
            builder.addQueryParams(rawInputs);
        } else {
            builder.setBody(rawInputs);
        }
    }

    void injectRawBody(Object openApiOperation, Map<String, Object> rawBody) {
        extractRequestSpecBuilder(openApiOperation).setBody(rawBody);
    }

    private static boolean hasQueryParams(String httpMethod) {
        return "GET".equalsIgnoreCase(httpMethod) || "DELETE".equalsIgnoreCase(httpMethod);
    }

    private static RequestSpecBuilder extractRequestSpecBuilder(Object openApiOperation) {
        try {
            Field field = openApiOperation.getClass().getDeclaredField(REQUEST_SPEC_FIELD);
            field.setAccessible(true);
            return (RequestSpecBuilder) field.get(openApiOperation);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossibile accedere al RequestSpecBuilder dell'operazione OpenAPI",
                    e
            );
        }
    }
}
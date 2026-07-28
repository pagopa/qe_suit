package it.pagopa.send.infrastructure.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public record ApiResponse(int statusCode, String rawBody) {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ApiResponse from(Response response) {
        return new ApiResponse(response.getStatusCode(), response.asString());
    }

    public boolean is2xxSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    public <T> T as(Class<T> type) {
        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(rawBody, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Impossibile convertire il body nel tipo " + type.getSimpleName(), e);
        }
    }
}

package it.pagopa.interop.new_arch.common.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public record ApiResponse(int statusCode, String rawBody) {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ApiResponse from(Response raResponse) {
        return new ApiResponse(raResponse.getStatusCode(), raResponse.asString());
    }

    public <T> T as(Class<T> clazz) {
        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(rawBody, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Impossibile convertire il body nel tipo " + clazz.getSimpleName(), e);
        }
    }

    public <T> T as(TypeReference<T> typeReference) {
        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(rawBody, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException("Impossibile convertire il body nel tipo generico richiesto", e);
        }
    }

    public String jsonPath(String path) {
        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }
        try {
            JsonNode node = MAPPER.readTree(rawBody);
            for (String part : path.split("\\.")) {
                node = node.get(part);
                if (node == null) return null;
            }
            return node.isValueNode() ? node.asText() : node.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
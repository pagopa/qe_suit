package it.pagopa.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.Response;

import java.util.Arrays;

public class ApiResponse extends RawResponse {

    private final int statusCode;
    private final Response apiResponse;

    public ApiResponse(Response raResponse) {
        super(is2xxSuccessful(raResponse), raResponse.asString());
        this.statusCode = raResponse.getStatusCode();
        this.apiResponse = raResponse;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        if (clazz == byte[].class) {
            return clazz.cast(asByteArray());
        }
        return apiResponse.as(clazz);
    }

    @Override
    public <T> T as(TypeReference<T> typeReference) {
        return apiResponse.as(typeReference.getType());
    }

    public boolean is2xxSuccessful() {
        return is2xxSuccessful(apiResponse);
    }

    private static boolean is2xxSuccessful(Response apiResponse) {
        return apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] asByteArray() {
        byte[] body = apiResponse.asByteArray();
        return Arrays.copyOf(body, body.length);
    }
}
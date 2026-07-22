package it.pagopa.interop.new_arch.common.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.Response;
import lombok.Getter;

public class ApiResponse extends RawResponse {

    @Getter
    private final int statusCode;
    private final Response apiResponse;

    public ApiResponse(Response raResponse) {
        super(raResponse.getStatusCode() >= 200 && raResponse.getStatusCode() < 300, raResponse.asString());
        this.statusCode = raResponse.getStatusCode();
        this.apiResponse = raResponse;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        return apiResponse.as(clazz);
    }

    @Override
    public <T> T as(TypeReference<T> typeReference) {
        return apiResponse.as(typeReference.getType());
    }

    public boolean is2xxSuccessful() {
        return apiResponse.getStatusCode() >= 200 && apiResponse.getStatusCode() < 300;
    }
}
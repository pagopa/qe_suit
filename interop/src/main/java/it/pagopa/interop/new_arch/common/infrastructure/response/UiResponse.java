package it.pagopa.interop.new_arch.common.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;

public class UiResponse extends RawResponse {
    private final Object response;

    public UiResponse(Object response) {
        super(response != null ? response.toString() : null);
        this.response = response;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        if (response == null) {
            return null;
        }
        return clazz.cast(response);
    }

    @Override
    public <T> T as(TypeReference<T> typeReference) {
        if (response == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T result = (T) response;
        return result;
    }
}

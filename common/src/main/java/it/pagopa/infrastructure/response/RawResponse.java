package it.pagopa.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;

public abstract class RawResponse {
    protected boolean isSuccess = true;
    protected final String rawContent;

    protected RawResponse(boolean isSuccess, String rawContent) {
        this.isSuccess = isSuccess;
        this.rawContent = rawContent;
    }

    public RawResponse(String rawContent) {
        this.rawContent = rawContent;
    }

    public abstract <T> T as(Class<T> clazz);

    public abstract <T> T as(TypeReference<T> typeReference);

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getRawContent() {
        return rawContent;
    }
}

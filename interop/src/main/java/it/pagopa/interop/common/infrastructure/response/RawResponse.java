package it.pagopa.interop.new_arch.common.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class RawResponse {
    protected boolean isSuccess = true;
    protected final String rawContent;

    protected RawResponse(boolean isSuccess, String rawContent) {
        this.isSuccess = isSuccess;
        this.rawContent = rawContent;
    }

    public abstract <T> T as(Class<T> clazz);

    public abstract <T> T as(TypeReference<T> typeReference);
}

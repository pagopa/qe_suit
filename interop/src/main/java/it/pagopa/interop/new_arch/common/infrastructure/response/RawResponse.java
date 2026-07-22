package it.pagopa.interop.new_arch.common.infrastructure.response;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class RawResponse {
    protected final boolean isSuccess;
    protected final String rawContent;

    public abstract <T> T as(Class<T> clazz);

    public abstract <T> T as(TypeReference<T> typeReference);
}

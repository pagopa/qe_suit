package it.pagopa.interop.common.infrastructure.template.action.context;

import it.pagopa.infrastructure.response.RawResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class BaseActionContext {
    protected final Supplier<RawResponse> responseSupplier;
    protected final Class<?> responseClass;
}

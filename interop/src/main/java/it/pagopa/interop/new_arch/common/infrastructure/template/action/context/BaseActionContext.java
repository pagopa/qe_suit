package it.pagopa.interop.new_arch.common.infrastructure.template.action.context;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class BaseActionContext {
    protected final Supplier<ApiResponse> responseSupplier;
    protected final Class<? extends Identifiable> modelClass;
    protected final Class<?> responseClass;
}

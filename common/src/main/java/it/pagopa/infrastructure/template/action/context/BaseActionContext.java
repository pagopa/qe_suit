package it.pagopa.infrastructure.template.action.context;

import it.pagopa.infrastructure.response.RawResponse;

import java.util.function.Supplier;

public class BaseActionContext {
    protected final Supplier<RawResponse> responseSupplier;
    protected final Class<?> responseClass;

    public BaseActionContext(Supplier<RawResponse> responseSupplier, Class<?> responseClass) {
        this.responseSupplier = responseSupplier;
        this.responseClass = responseClass;
    }

    public Supplier<RawResponse> getResponseSupplier() {
        return responseSupplier;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }
}

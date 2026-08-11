package it.pagopa.interop.common.infrastructure.template.action;

import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.infrastructure.response.RawResponse;

import java.util.function.Function;

public class ResolvedResponseFinalizer<Response>
        implements ResponseFinalizer<Response> {

    private final Response response;
    private final RawResponse raw;
    private final EntityStore entityStore;

    public ResolvedResponseFinalizer(Response response, RawResponse raw, EntityStore entityStore) {
        this.response = response;
        this.raw = raw;
        this.entityStore = entityStore;
    }

    @Override
    public <T> ResponseFinalizer<T> map(Function<? super Response, ? extends T> mapper) {
        T mappedResponse = mapper.apply(response);

        return new ResolvedResponseFinalizer<>(
                mappedResponse,
                raw,
                entityStore
        );
    }

    @Override
    public Response get() {
        return response;
    }

    @Override
    public RawResponse getRaw() {
        return raw;
    }

    @Override
    public EntityStore getEntityStore() {
        return entityStore;
    }
}
package it.pagopa.infrastructure.template.action;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.utils.async.PollingUtils;

import java.util.function.Function;

public final class PollingAction<Response> implements ResponseFinalizer<Response> {
    private final LastApiResponseStore lastApiResponseStore;
    private final EntityStore entityStore;

    private RawResponse raw;
    private PollingActionContext<? super Response> context;

    public PollingAction(LastApiResponseStore lastApiResponseStore, EntityStore entityStore) {
        this.lastApiResponseStore = lastApiResponseStore;
        this.entityStore = entityStore;
    }

    PollingAction<Response> handle(PollingActionContext<? super Response> context) {
        this.context = context;

        RawResponse firstResponse = context.getResponseSupplier().get();

        if (isSatisfied(firstResponse)) {
            this.raw = firstResponse;
        } else if (context.getTimeout() == null) {
            this.raw = PollingUtils.pollUntil(
                    context.getResponseSupplier(),
                    this::isSatisfied
            );
        } else {
            this.raw = PollingUtils.pollUntil(
                    context.getResponseSupplier(),
                    this::isSatisfied,
                    context.getTimeout(),
                    context.getInterval()
            );
        }

        updateLastApiResponse();

        return this;
    }

    PollingAction<Response> handleWithout(PollingActionContext<Response> context) {
        this.context = context;
        this.raw = context.getResponseSupplier().get();

        updateLastApiResponse();

        return this;
    }

    @Override
    public <T> ResponseFinalizer<T> map(Function<? super Response, ? extends T> mapper) {
        T mappedResponse = mapper.apply(get());
        return new ResolvedResponseFinalizer<>(
                mappedResponse,
                raw,
                entityStore
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response get() {
        if (context.getResponseClass() == Void.class) {
            return null;
        }

        return (Response) raw.as(context.getResponseClass());
    }

    @Override
    public RawResponse getRaw() {
        return raw;
    }

    @Override
    public EntityStore getEntityStore() {
        return entityStore;
    }

    public PollingActionContext<? super Response> getContext() {
        return context;
    }

    private boolean isSatisfied(RawResponse response) {
        if (response instanceof ApiResponse apiResponse) {
            return context.getPollingStrategy().isSatisfied(apiResponse);
        }

        return response != null;
    }

    private void updateLastApiResponse() {
        if (raw instanceof ApiResponse apiResponse) {
            lastApiResponseStore.setLastResponse(apiResponse);
        }
    }
}

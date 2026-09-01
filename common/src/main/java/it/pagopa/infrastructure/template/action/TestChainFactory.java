package it.pagopa.infrastructure.template.action;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.context.BaseActionContext;

import java.util.function.Supplier;

public final class TestChainFactory {
    private final LastApiResponseStore lastApiResponseStore;
    private final EntityStore entityStore;

    public TestChainFactory(LastApiResponseStore lastApiResponseStore, EntityStore entityStore) {
        this.lastApiResponseStore = lastApiResponseStore;
        this.entityStore = entityStore;
    }

    public <Response> TestChain<Response> build(Supplier<RawResponse> responseSupplier, Class<Response> responseClass) {
        BaseActionContext baseActionContext = new BaseActionContext(responseSupplier, responseClass);
        return new TestChain<>(
                baseActionContext,
                () -> new PollingAction<>(lastApiResponseStore, entityStore)
        );
    }
}

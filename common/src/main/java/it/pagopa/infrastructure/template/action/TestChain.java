package it.pagopa.infrastructure.template.action;

import it.pagopa.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;

import java.time.Duration;
import java.util.function.Supplier;

public final class TestChain<Response> {
    private final BaseActionContext baseActionContext;
    private final Supplier<PollingAction<Response>> pollingActionSupplier;

    public TestChain(
            BaseActionContext baseActionContext,
            Supplier<PollingAction<Response>> pollingActionSupplier) {
        this.baseActionContext = baseActionContext;
        this.pollingActionSupplier = pollingActionSupplier;
    }

    public PollingAction<Response> withPolling(PollingStrategy pollingStrategy) {
        var pollingContext = new PollingActionContext<>(baseActionContext, pollingStrategy, null, null);
        return pollingActionSupplier.get().handle(pollingContext);
    }

    public PollingAction<Response> withPolling(PollingStrategy pollingStrategy, Duration timeout, Duration interval) {
        var pollingContext = new PollingActionContext<>(baseActionContext, pollingStrategy, timeout, interval);
        return pollingActionSupplier.get().handle(pollingContext);
    }

    public PollingAction<Response> withoutPolling() {
        PollingActionContext<Response> pollingContext = new PollingActionContext<>(baseActionContext, resp -> true, null, null);
        return pollingActionSupplier.get().handleWithout(pollingContext);
    }
}

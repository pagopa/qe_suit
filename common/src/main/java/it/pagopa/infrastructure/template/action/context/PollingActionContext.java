package it.pagopa.infrastructure.template.action.context;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;

import java.time.Duration;

public class PollingActionContext<Response> extends BaseActionContext {
    private final PollingStrategy pollingStrategy;
    private final Duration timeout;
    private final Duration interval;

    public PollingActionContext(
            BaseActionContext context,
            PollingStrategy pollingStrategy,
            Duration timeout,
            Duration interval) {
        super(context.getResponseSupplier(), context.getResponseClass());
        this.pollingStrategy = pollingStrategy;
        this.timeout = timeout;
        this.interval = interval;
    }

    public PollingStrategy getPollingStrategy() {
        return pollingStrategy;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public Duration getInterval() {
        return interval;
    }
}

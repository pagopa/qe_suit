package it.pagopa.interop.new_arch.common.infrastructure.template.action.context;

import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import lombok.Getter;

import java.time.Duration;

@Getter
public class PollingActionContext<Response> extends BaseActionContext {

    private final PollingStrategy<Response> pollingStrategy;
    private final Duration timeout;
    private final Duration interval;

    public PollingActionContext(
            BaseActionContext context,
            PollingStrategy<Response> pollingStrategy,
            Duration timeout,
            Duration interval) {
        super(context.getResponseSupplier(), context.getModelClass(), context.getResponseClass());
        this.pollingStrategy = pollingStrategy;
        this.timeout = timeout;
        this.interval = interval;
    }
}
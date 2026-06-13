package it.pagopa.interop.common.service.template.action.context;

import it.pagopa.interop.common.service.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.domain.model.TestModel;
import lombok.Getter;

import java.time.Duration;

@Getter
public class PollingActionContext<Entity, Model extends TestModel> extends BaseActionContext<Entity, Model> {

    private final PollingStrategy<? super Entity> pollingStrategy;
    private final Duration timeout;
    private final Duration interval;
    private final BaseActionContext<Entity, Model> baseActionContext;

    public PollingActionContext(
            BaseActionContext<Entity,Model> context,
            PollingStrategy<? super Entity> pollingStrategy,
            Duration timeout,
            Duration interval) {

        super(context.getResponseSupplier(), context.getMapper());
        this.baseActionContext = context;
        this.pollingStrategy = pollingStrategy;
        this.timeout = timeout;
        this.interval = interval;
    }
}
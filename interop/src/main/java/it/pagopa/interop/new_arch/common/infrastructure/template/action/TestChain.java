package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.PollingActionContext;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestChain<Entity, Model extends Identifiable> {

    private BaseActionContext<Entity, Model> baseActionContext;
    @Setter(onMethod_ = {@Autowired}) private ObjectProvider<PollingAction<Entity, Model>> pollingActionProvider;

    TestChain<Entity, Model> handle(BaseActionContext<Entity, Model> baseActionContext) {
        this.baseActionContext = baseActionContext;
        return this;
    }

    public PollingAction<Entity, Model> withPolling(PollingStrategy<? super Entity> pollingStrategy) {
        var pollingContext = new PollingActionContext<>(baseActionContext, pollingStrategy, null, null);
        return pollingActionProvider.getObject().handle(pollingContext);
    }

    public PollingAction<Entity, Model> withPolling(PollingStrategy<? super Entity> pollingStrategy, Duration timeout, Duration interval) {
        var pollingContext = new PollingActionContext<>(baseActionContext, pollingStrategy, timeout, interval);
        return pollingActionProvider.getObject().handle(pollingContext);
    }

    public PollingAction<Entity, Model> withoutPolling() {
        var pollingContext = new PollingActionContext<>(baseActionContext, null, null, null);
        return pollingActionProvider.getObject().handleWithout(pollingContext);
    }
}
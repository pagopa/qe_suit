package it.pagopa.interop.new_arch.common.infrastructure.template.action;

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
public class TestChain<Response> {

    private BaseActionContext baseActionContext;
    @Setter(onMethod_ = {@Autowired}) private ObjectProvider<PollingAction<Response>> pollingActionProvider;

    TestChain<Response> handle(BaseActionContext baseActionContext) {
        this.baseActionContext = baseActionContext;
        return this;
    }


    public PollingAction<Response> withPolling(PollingStrategy pollingStrategy) {
        var pollingContext = new PollingActionContext<>(baseActionContext, pollingStrategy, null, null);
        return pollingActionProvider.getObject().handle(pollingContext);
    }

    public PollingAction<Response> withPolling(PollingStrategy pollingStrategy, Duration timeout, Duration interval) {
        var pollingContext = new PollingActionContext<>(baseActionContext, pollingStrategy, timeout, interval);
        return pollingActionProvider.getObject().handle(pollingContext);
    }

    public PollingAction<Response> withoutPolling() {
        PollingActionContext<Response> pollingContext = new PollingActionContext<>(baseActionContext, resp -> true, null, null);
        return pollingActionProvider.getObject().handleWithout(pollingContext);
    }

}
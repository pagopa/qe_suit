package it.pagopa.interop.bff.service.action;

import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestChain<Entity> {

    private Supplier<ResponseEntity<Entity>> responseSupplier;
    @Setter(onMethod_ = {@Autowired}) private ObjectProvider<PollingAction<Entity>> pollingActionProvider;
    @Setter(onMethod_ = {@Autowired}) private ObjectProvider<ResponseAction<Entity>> responseActionProvider;

    public TestChain<Entity> handle(Supplier<ResponseEntity<Entity>> responseSupplier) {
        this.responseSupplier = responseSupplier;
        return this;
    }

    public PollingAction<Entity> withPolling() {
        return pollingActionProvider.getObject().handle(responseSupplier);
    }

    public PollingAction<Entity> withPolling(Duration timeout, Duration interval) {
        return pollingActionProvider.getObject().handle(responseSupplier, timeout, interval);
    }

    public ResponseAction<Entity> withoutPolling() {
        ResponseEntity<Entity> response = responseSupplier.get();
        return responseActionProvider.getObject().handle(response);
    }
}
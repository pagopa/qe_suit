package it.pagopa.interop.bff.service.action;

import it.pagopa.interop.common.utils.PollingUtils;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Entity> {

    private Supplier<ResponseEntity<Entity>> responseSupplier;
    private Duration timeout;
    private Duration interval;

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<ResponseAction<Entity>> responseActionProvider;

    PollingAction<Entity> handle(Supplier<ResponseEntity<Entity>> responseSupplier) {
        return handle(responseSupplier, Duration.ofSeconds(10), Duration.ofSeconds(1));
    }

    PollingAction<Entity> handle(Supplier<ResponseEntity<Entity>> responseSupplier, Duration timeout, Duration interval) {
        this.responseSupplier = responseSupplier;
        this.timeout = timeout;
        this.interval = interval;
        return this;
    }

    public ResponseAction<Entity> until(BiPredicate<HttpStatusCode, Entity> assertion) {
        ResponseEntity<Entity> finalResponse = PollingUtils.pollUntil(responseSupplier, r -> r != null && assertion.test(r.getStatusCode(), r.getBody()), timeout, interval);
        return responseActionProvider.getObject().handle(finalResponse);
    }

    public ResponseAction<Entity> untilSuccess() {
        ResponseEntity<Entity> finalResponse = PollingUtils.pollUntil(responseSupplier, r -> r != null && r.getStatusCode().is2xxSuccessful(), timeout, interval);
        return responseActionProvider.getObject().handle(finalResponse);
    }


    public ResponseAction<Entity> until(Predicate<HttpStatusCode> assertion) {
        ResponseEntity<Entity> finalResponse = PollingUtils.pollUntil(responseSupplier, response -> response != null && assertion.test(response.getStatusCode()), timeout, interval);
        return responseActionProvider.getObject().handle(finalResponse);
    }

    public ResponseAction<Entity> untilSuccessAnd(Predicate<Entity> assertion) {
        ResponseEntity<Entity> finalResponse = PollingUtils.pollUntil(responseSupplier, response -> response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null && assertion.test(response.getBody()), timeout, interval);
        return responseActionProvider.getObject().handle(finalResponse);
    }

    public ResponseAction<Entity> untilError() {
        ResponseEntity<Entity> finalResponse = PollingUtils.pollUntil(responseSupplier, response -> response != null && !response.getStatusCode().is2xxSuccessful(), timeout, interval);
        return responseActionProvider.getObject().handle(finalResponse);
    }
}
package it.pagopa.interop.bff.service.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.bff.service.action.strategy.AssertionStrategy;
import it.pagopa.interop.bff.service.action.strategy.PollingStrategy;
import it.pagopa.interop.common.domain.context.ScenarioContext;
import it.pagopa.interop.common.domain.model.DomainModelRegistry;
import it.pagopa.interop.common.domain.model.TestModel;
import it.pagopa.interop.common.utils.PollingUtils;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Entity> implements Finalizer {

    @Setter(onMethod_ = {@Autowired}) private AssertAction assertAction;
    @Setter(onMethod_ = {@Autowired}) private ScenarioContext scenarioContext;
    @Setter(onMethod_ = {@Autowired}) private DomainModelRegistry domainModelRegistry;
    @Setter(onMethod_ = {@Autowired}) private ObjectMapper objectMapper;
    private ResponseEntity<Entity> finalResponse;

    PollingAction<Entity> handle(Supplier<ResponseEntity<Entity>> responseSupplier, PollingStrategy<? super Entity> pollingStrategy) {
        return handle(responseSupplier, pollingStrategy, Duration.ofSeconds(10), Duration.ofSeconds(1));
    }

    PollingAction<Entity> handle(Supplier<ResponseEntity<Entity>> responseSupplier, PollingStrategy<? super Entity> pollingStrategy, Duration timeout, Duration interval) {
        ResponseEntity<Entity> response = PollingUtils.pollUntil(
                responseSupplier,
                r -> r != null && pollingStrategy.isSatisfied(r.getStatusCode(), r.getBody()),
                timeout,
                interval
        );

        return handle(response);
    }

    PollingAction<Entity> handle(ResponseEntity<Entity> finalResponse) {
        this.finalResponse = finalResponse;
        this.syncContext(finalResponse);
        return this;
    }

    public AssertAction andAssert(AssertionStrategy<? super Entity> assertionStrategy) {
        return assertAction.handle(finalResponse, assertionStrategy);
    }

    private void syncContext(ResponseEntity<Entity> response) {
        scenarioContext.upsert(response);

        Entity body = response.getBody();
        if (response.getStatusCode().is2xxSuccessful() && body != null) {
            Class<? extends TestModel> domainClass = domainModelRegistry.getDomainClassFor(body.getClass());

            if (domainClass != null) {
                TestModel domainModel = objectMapper.convertValue(body, domainClass);
                scenarioContext.upsert(domainModel);
            } else if (body instanceof TestModel testModel) {
                scenarioContext.upsert(testModel);
            } else {
                scenarioContext.upsert(body);
            }
        }
    }
}
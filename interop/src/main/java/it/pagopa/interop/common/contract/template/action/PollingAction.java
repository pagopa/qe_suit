package it.pagopa.interop.common.contract.template.action;

import it.pagopa.interop.common.contract.template.action.context.BaseActionContext;
import it.pagopa.interop.common.contract.template.action.context.PollingActionContext;
import it.pagopa.interop.common.contract.template.action.strategy.AssertionStrategy;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.common.utils.PollingUtils;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PollingAction<Entity, Model extends Identifiable> implements Finalizer<Entity, Model> {

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<AssertAction<Entity, Model>> assertActionProvider;
    @Setter(onMethod_ = {@Autowired})
    private ScenarioContext scenarioContext;
    private BaseActionContext<Entity, Model> baseActionContext;
    private ResponseEntity<Entity> finalResponse;

    PollingAction<Entity, Model> handle(PollingActionContext<Entity, Model> context) {
        this.baseActionContext = context.getBaseActionContext();
        this.finalResponse = PollingUtils.pollUntil(
                context.getResponseSupplier(),
                r -> context.getPollingStrategy().isSatisfied(r.getStatusCode(), r.getBody()),
                context.getTimeout() != null ? context.getTimeout() : Duration.ofSeconds(10),
                context.getInterval() != null ? context.getInterval() : Duration.ofSeconds(1)
        );

        return this;
    }

    PollingAction<Entity, Model> handleWithout(PollingActionContext<Entity, Model> context) {
        this.baseActionContext = context.getBaseActionContext();
        this.finalResponse = context.getResponseSupplier().get();
        return this;
    }

    public AssertAction<Entity, Model> andAssertThat(AssertionStrategy<? super Entity> assertionStrategy) {
        return assertActionProvider.getObject().handle(finalResponse, baseActionContext, assertionStrategy);
    }

    public PollingAction<Entity, Model> andUpdateContext(String... alias) {
        List<? extends Identifiable> models = baseActionContext.getMapper().apply(finalResponse.getBody());

        if (alias.length > models.size()) {
            throw new IllegalArgumentException("The given alias exceeds the maximum number of test models");
        }

        List<ScenarioContext.ContextEntry<? extends Identifiable>> contextEntries = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            Identifiable model = models.get(i);
            String modelAlias = i < alias.length ? alias[i] : null;
            contextEntries.add(new ScenarioContext.ContextEntry<>(model, modelAlias));
        }

        scenarioContext.setLastResponseEntity(finalResponse);
        scenarioContext.upsert(contextEntries);

        return this;
    }

    @Override
    public ResponseEntity<Entity> getResponse() {
        return finalResponse;
    }

    @Override
    public Model getModel() {
        List<Model> models = baseActionContext.getMapper().apply(finalResponse.getBody());
        return models.get(0);
    }

    @Override
    public List<Model> getModels() {
        return baseActionContext.getMapper().apply(finalResponse.getBody());
    }

}
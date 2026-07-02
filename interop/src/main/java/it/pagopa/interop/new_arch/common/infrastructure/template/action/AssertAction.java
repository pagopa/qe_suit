package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.common.contract.model.Identifiable;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.AssertionStrategy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AssertAction<Entity, Model extends Identifiable> implements Finalizer<Entity, Model> {

    private ResponseEntity<Entity> finalResponse;
    private BaseActionContext<Entity, Model> baseActionContext;

    AssertAction<Entity, Model> handle(ResponseEntity<Entity> response, BaseActionContext<Entity, Model> baseContext, AssertionStrategy<? super Entity> strategy) {
        this.finalResponse = response;
        this.baseActionContext = baseContext;
        strategy.assertThat(response);
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
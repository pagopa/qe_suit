package it.pagopa.interop.common.service.template.action;

import it.pagopa.interop.common.service.template.action.context.BaseActionContext;
import it.pagopa.interop.common.domain.model.TestModel;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class TestChainFactory {

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<TestChain> testActionChainProvider;

    @SuppressWarnings("unchecked")
    public <Entity, Model extends TestModel> TestChain<Entity, Model> build(Supplier<ResponseEntity<Entity>> httpCall, Function<Entity, List<Model>> mapper) {
        var baseActionContext = new BaseActionContext<>(httpCall, mapper);
        return testActionChainProvider.getObject().handle(baseActionContext);
    }
}

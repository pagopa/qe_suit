package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TestChainFactory {

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<TestChain> testActionChainProvider;

    @SuppressWarnings("unchecked")
    public <Response, Model extends Identifiable> TestChain<Response, Model> build(Supplier<ApiResponse> httpCall, Class<Model> modelClass, Class<?> responseClass) {
        var baseActionContext = new BaseActionContext(httpCall, modelClass, responseClass);
        return testActionChainProvider.getObject().handle(baseActionContext);
    }
}

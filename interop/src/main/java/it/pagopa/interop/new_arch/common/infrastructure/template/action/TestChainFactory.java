package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.response.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
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
    public <Response> TestChain<Response> build(Supplier<ApiResponse> httpCall, Class<?> responseClass) {
        var baseActionContext = new BaseActionContext(httpCall, responseClass);
        return testActionChainProvider.getObject().handle(baseActionContext);
    }
}

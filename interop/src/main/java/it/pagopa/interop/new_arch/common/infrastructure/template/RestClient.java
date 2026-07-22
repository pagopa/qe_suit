package it.pagopa.interop.new_arch.common.infrastructure.template;

import io.restassured.response.Response;
import it.pagopa.interop.new_arch.common.infrastructure.response.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChainFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class RestClient {
    @Getter
    @Setter(onMethod_ = {@Autowired})
    protected TestChainFactory chainFactory;

    protected <RESPONSE> TestChain<RESPONSE> execute(
            Supplier<Response> apiCall,
            Class<RESPONSE> responseClass) {

        return chainFactory.build(() -> ApiResponse.from(apiCall.get()), responseClass);
    }
}

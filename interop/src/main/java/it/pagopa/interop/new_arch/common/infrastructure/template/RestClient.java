package it.pagopa.interop.new_arch.common.infrastructure.template;

import io.restassured.response.Response;
import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;
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

    protected <RESPONSE, MODEL extends Identifiable> TestChain<RESPONSE, MODEL> execute(
            Supplier<Response> apiCall,
            Class<RESPONSE> responseClass,
            Class<MODEL> domainClass) {

        return chainFactory.build(() -> ApiResponse.from(apiCall.get()), domainClass, responseClass);
    }
}

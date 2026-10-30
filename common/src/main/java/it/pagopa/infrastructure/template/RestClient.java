package it.pagopa.infrastructure.template;

import io.restassured.response.Response;
import io.restassured.response.Response;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;

import java.util.function.Function;
import java.util.function.Supplier;

public class RestClient {
    protected final TestChainFactory chainFactory;

    public RestClient(TestChainFactory chainFactory) {
        this.chainFactory = chainFactory;
    }

    protected <RESPONSE> TestChain<RESPONSE> execute(
            Supplier<Response> apiCall,
            Class<RESPONSE> responseClass) {

        return chainFactory.build(() -> new ApiResponse(apiCall.get()), responseClass);
    }

    protected <RESPONSE> TestChain<RESPONSE> execute(
            Supplier<Response> apiCall,
            Class<RESPONSE> responseClass,
            Function<Response, RESPONSE> responseMapper) {

        return chainFactory.build(() -> {
            Response response = apiCall.get();
            return new ApiResponse(response) {
                @Override
                public <T> T as(Class<T> clazz) {
                    if (clazz == responseClass) {
                        return clazz.cast(responseMapper.apply(response));
                    }
                    return super.as(clazz);
                }
            };
        }, responseClass);
    }
}

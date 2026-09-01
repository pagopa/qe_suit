package it.pagopa.infrastructure.template;

import io.restassured.response.Response;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;

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
}

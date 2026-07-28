package it.pagopa.send.infrastructure.template;

import io.restassured.response.Response;

import java.util.function.Supplier;

/**
 * Base per i client REST che eseguono operazioni generate da openapi-generator (rest-assured
 * library) e vogliono ottenere un {@link TestChain}, con eventuale possibilità di polling.
 */
public abstract class RestClient {

    protected <T> TestChain<T> execute(Supplier<Response> apiCall, Class<T> responseType) {
        return new TestChain<>(apiCall, responseType);
    }
}

package it.pagopa.interop.common.infrastructure.http.contract.engine;

import java.util.Map;

public abstract class ContractTestEngine {

    private final OpenApiRequestInjector requestInjector = new OpenApiRequestInjector();

    protected <T extends java.io.Serializable> Fuzz<T> fuzz(T validPayload) {
        return new Fuzz<>(validPayload, Map.of());
    }

    protected <T extends java.io.Serializable> Fuzz<T> fuzz(T validPayload, Map<String, Object> validInputs) {
        return new Fuzz<>(validPayload, validInputs);
    }

    protected void injectRawInputs(Object openApiOperation, Map<String, Object> rawInputs, String httpMethod) {
        requestInjector.injectRawInputs(openApiOperation, rawInputs, httpMethod);
    }

    protected void injectRawBody(Object openApiOperation, Map<String, Object> rawBody) {
        requestInjector.injectRawBody(openApiOperation, rawBody);
    }
}
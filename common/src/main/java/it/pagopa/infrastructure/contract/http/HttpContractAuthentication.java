package it.pagopa.infrastructure.contract.http;

@FunctionalInterface
public interface HttpContractAuthentication {
    void authenticate();

    static HttpContractAuthentication noOp() {
        return () -> {
        };
    }
}

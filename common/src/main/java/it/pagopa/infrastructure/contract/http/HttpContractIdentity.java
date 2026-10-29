package it.pagopa.infrastructure.contract.http;

public record HttpContractIdentity(
        String name,
        HttpContractAuthentication authentication
) {
}

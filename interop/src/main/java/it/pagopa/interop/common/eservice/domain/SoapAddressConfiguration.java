package it.pagopa.interop.common.eservice.domain;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Objects;

public record SoapAddressConfiguration(
        @JsonAlias("servizio") String service,
        @JsonAlias("porta") String port,
        @JsonAlias("indirizzo") String address
) {
    public SoapAddressConfiguration {
        Objects.requireNonNull(service);
        Objects.requireNonNull(port);
        Objects.requireNonNull(address);
    }
}

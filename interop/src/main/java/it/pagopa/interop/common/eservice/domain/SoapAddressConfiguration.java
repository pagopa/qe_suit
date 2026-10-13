package it.pagopa.interop.common.eservice.domain;

import java.util.Objects;

public record SoapAddressConfiguration(String service, String port, String address) {
    public SoapAddressConfiguration {
        Objects.requireNonNull(service);
        Objects.requireNonNull(port);
        Objects.requireNonNull(address);
    }
}

package it.pagopa.interop.common.eservice.domain;

import java.util.List;
import java.util.Objects;

public record RestInterfaceConfiguration(
        List<String> serverUrls,
        String contactName,
        String contactEmail,
        String contactUrl,
        String termsAndConditionsUrl
) {
    public RestInterfaceConfiguration {
        serverUrls = List.copyOf(serverUrls);
        Objects.requireNonNull(contactName);
        Objects.requireNonNull(contactEmail);
    }
}

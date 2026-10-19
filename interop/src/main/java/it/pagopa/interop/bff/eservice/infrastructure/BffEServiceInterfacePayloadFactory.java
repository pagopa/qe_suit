package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.common.eservice.domain.RestInterfaceConfiguration;
import it.pagopa.interop.common.eservice.domain.SoapAddressConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BffEServiceInterfacePayloadFactory {
    private BffEServiceInterfacePayloadFactory() {}

    public static Map<String, Object> soapPayload(List<SoapAddressConfiguration> addresses) {
        List<Map<String, String>> urls = addresses.stream()
                .map(address -> Map.of("url", address.address()))
                .toList();
        return Map.of("serverUrls", urls);
    }

    public static Map<String, Object> restPayload(RestInterfaceConfiguration configuration) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("serverUrls", configuration.serverUrls().stream().map(url -> Map.of("url", url)).toList());
        payload.put("contactName", configuration.contactName());
        payload.put("contactEmail", configuration.contactEmail());
        payload.put("contactUrl", configuration.contactUrl());
        payload.put("termsAndConditionsUrl", configuration.termsAndConditionsUrl());
        return payload;
    }
}

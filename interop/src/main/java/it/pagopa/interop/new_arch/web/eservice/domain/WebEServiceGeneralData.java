package it.pagopa.interop.new_arch.web.eservice.domain;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;

import java.util.UUID;

public record WebEServiceGeneralData(EServiceSeed eservice) {
    public static WebEServiceGeneralData buildDefault() {
        return new WebEServiceGeneralData(
                new EServiceSeed()
                        .name("Test eService " + UUID.randomUUID().toString().substring(0, 8))
                        .description("Test eService description")
                        .asyncExchange(false)
                        .personalData(false)
                        .technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER)
        );
    }
}

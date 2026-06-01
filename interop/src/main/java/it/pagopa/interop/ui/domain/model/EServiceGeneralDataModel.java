package it.pagopa.interop.ui.domain.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;

import java.util.UUID;

public record EServiceGeneralDataModel(EServiceSeed eservice) {
    public static EServiceGeneralDataModel buildDefault() {
        return new EServiceGeneralDataModel(
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

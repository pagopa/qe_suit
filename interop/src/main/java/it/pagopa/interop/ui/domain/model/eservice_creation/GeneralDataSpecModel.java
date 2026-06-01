package it.pagopa.interop.ui.domain.model.eservice_creation;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;

import java.util.UUID;

public record GeneralDataSpecModel(EServiceSeed eservice) {
    public static GeneralDataSpecModel buildDefault() {
        return new GeneralDataSpecModel(
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

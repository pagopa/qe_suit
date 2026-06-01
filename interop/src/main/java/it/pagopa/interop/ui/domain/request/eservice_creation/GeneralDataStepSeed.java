package it.pagopa.interop.ui.domain.request.eservice_creation;

import it.pagopa.interop.ui.domain.request.UiRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;

import java.util.UUID;

public record GeneralDataStepSeed(EServiceSeed eservice) implements UiRequest {
    public static GeneralDataStepSeed buildDefault() {
        return new GeneralDataStepSeed(
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

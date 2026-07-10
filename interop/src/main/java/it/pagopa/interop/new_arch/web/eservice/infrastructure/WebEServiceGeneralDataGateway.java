package it.pagopa.interop.new_arch.web.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.new_arch.web.eservice.domain.WebEServiceGeneralData;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.suit.component.creation_wizard.GeneralDataWizard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceGeneralDataGateway {

    private final GeneralDataWizard generalDataWizard;

    public void fillWizard(WebEServiceGeneralData model) {
        validateAsyncExchangeMode(model);

        generalDataWizard
                .setName(model.eservice().getName())
                .setDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setPersonalData(model.eservice().getPersonalData())
                .setMode(model.eservice().getMode());
    }

    public WebEServiceGeneralData readWizard() {
        EServiceSeed seed = new EServiceSeed()
                .name(generalDataWizard.name().read())
                .description(generalDataWizard.description().read())
                .technology(generalDataWizard.getTechnology())
                .asyncExchange(generalDataWizard.getAsyncExchange())
                .mode(generalDataWizard.getMode())
                .personalData(generalDataWizard.getPersonalData());

        return new WebEServiceGeneralData(seed);
    }

    private void validateAsyncExchangeMode(WebEServiceGeneralData model) {
        boolean hasAsyncExchange = Boolean.TRUE.equals(model.eservice().getAsyncExchange());
        if (hasAsyncExchange && model.eservice().getMode() != null) {
            throw new IllegalStateException(
                    "Cannot set MODE for an async eService, but got: " + model.eservice().getMode()
            );
        }
    }
}
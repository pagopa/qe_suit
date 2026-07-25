package it.pagopa.interop.new_arch.web.eservice.infrastructure;

import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceMode;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.new_arch.web.eservice.application.WebEServiceGeneralData;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.new_arch.web.eservice.infrastructure.page.component.creation_wizard.GeneralDataWizard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceGeneralDataGateway {

    private final EServiceCreationPage eServiceCreationPage;

    public void fillEServiceGeneralData(WebEServiceGeneralData model) {
        validateAsyncExchangeMode(model);
        GeneralDataWizard generalDataWizard = eServiceCreationPage.generalDataStep();

        generalDataWizard
                .setName(model.eservice().getName())
                .setDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setPersonalData(model.eservice().getPersonalData())
                .setMode(model.eservice().getMode());

        eServiceCreationPage.saveDraftButton().click();
    }

    public EService readEServiceGeneralData() {
        GeneralDataWizard generalDataWizard = eServiceCreationPage.generalDataStep();

        return EService.builder()
                .name(generalDataWizard.name().read())
                .description(generalDataWizard.description().read())
                .technology(EServiceTechnology.valueOf(generalDataWizard.getTechnology().name()))
                .asyncExchange(generalDataWizard.getAsyncExchange())
                .mode(EServiceMode.valueOf(generalDataWizard.getMode().name()))
                .personalData(generalDataWizard.getPersonalData())
                .build();
    }

    private void validateAsyncExchangeMode(WebEServiceGeneralData model) {
        boolean hasAsyncExchange = Boolean.TRUE.equals(model.eservice().getAsyncExchange());
        if (hasAsyncExchange) {
            throw new IllegalStateException(
                    "Cannot set MODE for an async eService, but got: " + model.eservice().getMode()
            );
        }
    }
}
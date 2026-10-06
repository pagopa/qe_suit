package it.pagopa.interop.web.eservice.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.web.eservice.application.WebEServiceGeneralData;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.GeneralDataForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebEServiceGeneralDataGateway {

    private final EServiceCreatePage eServiceCreatePage;

    public void fillEServiceGeneralData(WebEServiceGeneralData model) {
        validateAsyncExchangeMode(model);
        GeneralDataForm generalDataForm = eServiceCreatePage.generalDataForm();

        generalDataForm
                .setEServiceName(model.eservice().getName())
                .setEServiceDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setUsePersonalData(Boolean.TRUE.equals(model.eservice().getPersonalData()))
                .setMode(model.eservice().getMode())
                .saveDraft();
    }

    public EService readEServiceGeneralData() {
        GeneralDataForm generalDataWizard = eServiceCreatePage.generalDataForm();

        return EService.builder()
                .name(generalDataWizard.getEServiceName())
                .description(generalDataWizard.getEServiceDescription())
                .technology(EServiceTechnology.valueOf(generalDataWizard.getEServiceTechnology().name()))
                .asyncExchange(generalDataWizard.getAsyncExchange())
                .mode(EServiceMode.valueOf(generalDataWizard.getEServiceMode().name()))
                .personalData(generalDataWizard.getUsePersonalData())
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
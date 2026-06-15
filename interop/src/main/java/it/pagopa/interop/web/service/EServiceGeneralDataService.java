package it.pagopa.interop.web.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.web.model.EServiceGeneralData;
import it.pagopa.interop.web.page.eservice.creation.wizard.GeneralDataWizard;
import it.pagopa.interop.common.contract.template.ui.UiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceGeneralDataService implements UiService<EServiceGeneralData, GeneralDataWizard> {

    private final GeneralDataWizard generalDataWizardComponent;

    @Override
    public EServiceGeneralData doDefaultModel() {
        return EServiceGeneralData.buildDefault();
    }

    @Override
    public void doFill(EServiceGeneralData model) {
        boolean hasAsyncExchange = Boolean.TRUE.equals(model.eservice().getAsyncExchange());

        generalDataWizardComponent
                .setName(model.eservice().getName())
                .setDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setPersonalData(model.eservice().getPersonalData());

        // Se l'eservice è async il campo MODE in interfaccia è disabilitato
        if (hasAsyncExchange && model.eservice().getMode() != null)
            throw new IllegalStateException("Cannot set MODE for an async eService, but got: " + model.eservice().getMode());

        generalDataWizardComponent.setMode(model.eservice().getMode());
    }

    @Override
    public GeneralDataWizard getComponent() {
        return generalDataWizardComponent;
    }

    @Override
    public EServiceGeneralData mapToModel(GeneralDataWizard component) {
        EServiceSeed seed =
                new EServiceSeed()
                        .name(component.name().read())
                        .description(component.description().read())
                        .technology(component.getTechnology())
                        .asyncExchange(component.getAsyncExchange())
                        .mode(component.getMode())
                        .personalData(component.getPersonalData());

        return new EServiceGeneralData(seed);
    }

}

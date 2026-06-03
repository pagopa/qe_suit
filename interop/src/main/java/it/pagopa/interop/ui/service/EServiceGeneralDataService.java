package it.pagopa.interop.ui.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.ui.domain.model.EServiceGeneralDataModel;
import it.pagopa.interop.ui.page.eservice_creation.step.GeneralDataStep;
import it.pagopa.interop.ui.service.template.UiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceGeneralDataService implements UiService<EServiceGeneralDataModel, GeneralDataStep> {

    private final GeneralDataStep generalDataStepComponent;

    @Override
    public EServiceGeneralDataModel doDefaultModel() {
        return EServiceGeneralDataModel.buildDefault();
    }

    @Override
    public void doFill(EServiceGeneralDataModel model) {
        boolean hasAsyncExchange = Boolean.TRUE.equals(model.eservice().getAsyncExchange());

        generalDataStepComponent
                .setName(model.eservice().getName())
                .setDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setPersonalData(model.eservice().getPersonalData());

        // Se l'eservice è async il campo MODE in interfaccia è disabilitato
        if (hasAsyncExchange && model.eservice().getMode() != null)
            throw new IllegalStateException("Cannot set MODE for an async eService, but got: " + model.eservice().getMode());

        generalDataStepComponent.setMode(model.eservice().getMode());
    }

    @Override
    public GeneralDataStep getComponent() {
        return generalDataStepComponent;
    }

    @Override
    public EServiceGeneralDataModel mapToModel(GeneralDataStep component) {
        EServiceSeed seed =
                new EServiceSeed()
                        .name(component.name().read())
                        .description(component.description().read())
                        .technology(component.getTechnology())
                        .asyncExchange(component.getAsyncExchange())
                        .mode(component.getMode())
                        .personalData(component.getPersonalData());

        return new EServiceGeneralDataModel(seed);
    }

}

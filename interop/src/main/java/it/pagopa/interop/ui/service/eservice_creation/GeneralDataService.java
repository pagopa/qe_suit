package it.pagopa.interop.ui.service.eservice_creation;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.ui.domain.model.eservice_creation.GeneralDataSpecModel;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.GeneralDataStep;
import it.pagopa.interop.ui.service.template.UiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GeneralDataService implements UiService<GeneralDataSpecModel, GeneralDataStep> {

    private final GeneralDataStep generalDataStepComponent;

    @Override
    public GeneralDataSpecModel doDefaultModel() {
        return GeneralDataSpecModel.buildDefault();
    }

    @Override
    public void doFill(GeneralDataSpecModel model) {
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
    public GeneralDataSpecModel mapToModel(GeneralDataStep component) {
        EServiceSeed seed =
                new EServiceSeed()
                        .name(component.name().read())
                        .description(component.description().read())
                        .technology(component.getTechnology())
                        .asyncExchange(component.getAsyncExchange())
                        .mode(component.getMode())
                        .personalData(component.getPersonalData());

        return new GeneralDataSpecModel(seed);
    }

}

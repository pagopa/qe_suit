package it.pagopa.interop.ui.service.eservice_creation;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.GeneralDataStepComponent;
import it.pagopa.interop.ui.domain.request.eservice_creation.GeneralDataStepSeed;
import it.pagopa.interop.ui.service.template.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralDataService implements UiService<GeneralDataStepSeed, GeneralDataStepComponent, ProducerEServiceDetails> {

    private final GeneralDataStepComponent generalDataStepComponent;

    @Autowired
    public GeneralDataService(EServiceCreationPage creationPage) {
        this.generalDataStepComponent = creationPage.generalDataStep();
    }

    @Override
    public GeneralDataStepSeed doDefaultRequest() {
        return GeneralDataStepSeed.buildDefault();
    }

    @Override
    public void doFill(GeneralDataStepSeed seed) {
        boolean hasAsyncExchange = seed.eservice().getAsyncExchange() != null;

        generalDataStepComponent
                .setName(seed.eservice().getName())
                .setDescription(seed.eservice().getDescription())
                .setAsyncExchange(seed.eservice().getAsyncExchange())
                .setTechnology(seed.eservice().getTechnology())
                .setPersonalData(seed.eservice().getPersonalData());

        // Se l'eservice è async il campo MODE in interfaccia è disabilitato
        if (hasAsyncExchange && seed.eservice().getMode() != null)
            throw new IllegalStateException("Cannot set MODE for an async eService, but got: " + seed.eservice().getMode());

        generalDataStepComponent.setMode(seed.eservice().getMode());
    }

    @Override
    public GeneralDataStepComponent getComponent() {
        return generalDataStepComponent;
    }

    @Override
    public ProducerEServiceDetails mapToModel(GeneralDataStepComponent generalDataForm) {
        ProducerEServiceDetails details = new ProducerEServiceDetails();

        // 1. Campi di testo semplici
        details.setName(generalDataForm.name().read());
        details.setDescription(generalDataForm.description().read());

        // 2. Mapping della Technology (REST / SOAP)
        String selectedTech = generalDataForm.technology().getSelected();
        if (selectedTech != null) {
            details.setTechnology(EServiceTechnology.fromValue(selectedTech));
        }

        // 3. Mapping del Mode (Eroga -> DELIVER, Riceve -> RECEIVE)
        String selectedMode = generalDataForm.mode().getSelected();
        if (selectedMode != null) {
            if (selectedMode.contains("Eroga")) {
                details.setMode(EServiceMode.DELIVER);
            } else if (selectedMode.contains("Riceve")) {
                details.setMode(EServiceMode.RECEIVE);
            }
        }

        // 4. Mapping dell'AsyncExchange (Asincrono -> true, Sincrono -> false)
        String selectedAsync = generalDataForm.asyncExchange().getSelected();
        if (selectedAsync != null) {
            details.setAsyncExchange(selectedAsync.contains("Asincrono"));
        }

        // 5. Mapping dei PersonalData (Eroga -> true, Non eroga -> false)
        String selectedPersonalData = generalDataForm.personalData().getSelected();
        if (selectedPersonalData != null) {
            if (selectedPersonalData.contains("Non eroga")) {
                details.setPersonalData(false);
            } else if (selectedPersonalData.contains("Eroga")) {
                details.setPersonalData(true);
            }
        }

        // Nota: I campi id, riskAnalysis, o i vari flag di delega
        // non sono presenti in questo step della UI, quindi vengono lasciati ignorati/null.

        return details;
    }
}

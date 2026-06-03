package it.pagopa.interop.ui.controller;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.ui.domain.model.EServiceGeneralDataModel;
import it.pagopa.interop.ui.domain.model.EServiceTechnicalModel;
import it.pagopa.interop.ui.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.service.EServiceGeneralDataService;
import it.pagopa.interop.ui.service.EServiceTechnicalDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceCreationController {

    private final EServiceGeneralDataService EServiceGeneralDataService;
    private final EServiceTechnicalDataService EServiceTechnicalDataService;
    private final EServiceCreationPage eServiceCreationPage;

    @When("clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceCreationPage.saveDraftButton().click();
    }

    @When("compila lo step 'Informazioni generali' con i valori di default ma specificando:")
    public void fillGeneralInformationWithOverrides(EServiceSeed eserviceSeed) {
        EServiceGeneralDataService.fillWithOverrides(new EServiceGeneralDataModel(eserviceSeed));
    }

    @When("compila lo step 'Informazioni generali' con i valori di default")
    public void fillGeneralInformation() {
        EServiceGeneralDataService.fill();
    }

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {
        EServiceTechnicalDataService.fill(EServiceTechnicalModel.buildEmpty());
    }

}
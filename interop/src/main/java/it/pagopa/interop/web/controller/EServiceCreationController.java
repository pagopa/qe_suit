package it.pagopa.interop.web.controller;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.web.model.EServiceGeneralData;
import it.pagopa.interop.web.model.EServiceTechnicalData;
import it.pagopa.interop.web.page.eservice.creation.EServiceCreationPage;
import it.pagopa.interop.web.service.EServiceGeneralDataService;
import it.pagopa.interop.web.service.EServiceTechnicalDataService;
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
        EServiceGeneralDataService.fillWithOverrides(new EServiceGeneralData(eserviceSeed));
    }

    @When("compila lo step 'Informazioni generali' con i valori di default")
    public void fillGeneralInformation() {
        EServiceGeneralDataService.fill();
    }

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {
        EServiceTechnicalDataService.fill(EServiceTechnicalData.buildEmpty());
    }

}
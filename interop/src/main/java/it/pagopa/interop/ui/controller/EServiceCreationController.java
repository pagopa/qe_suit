package it.pagopa.interop.ui.controller;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.ui.domain.model.eservice_creation.GeneralDataSpecModel;
import it.pagopa.interop.ui.domain.model.eservice_creation.TechnicalSpecModel;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.service.eservice_creation.GeneralDataService;
import it.pagopa.interop.ui.service.eservice_creation.TechnicalDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceCreationController {

    private final GeneralDataService generalDataService;
    private final TechnicalDataService technicalDataService;
    private final EServiceCreationPage eServiceCreationPage;

    @When("clicca sul button 'Salva bozza e prosegui'")
    public void saveDraft() {
        eServiceCreationPage.saveDraftButton().click();
    }

    @When("compila lo step 'Informazioni generali' con i valori di default ma specificando:")
    public void fillGeneralInformationWithOverrides(EServiceSeed eserviceSeed) {
        generalDataService.fillWithOverrides(new GeneralDataSpecModel(eserviceSeed));
    }

    @When("compila lo step 'Informazioni generali' con i valori di default")
    public void fillGeneralInformation() {
        generalDataService.fill();
    }

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {
        technicalDataService.fill(TechnicalSpecModel.buildEmpty());
    }

}
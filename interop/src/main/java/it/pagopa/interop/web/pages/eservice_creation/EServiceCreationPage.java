package it.pagopa.interop.web.pages.eservice_creation;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.utils.DelayUtils;
import it.pagopa.interop.web.component.Button;
import it.pagopa.interop.web.pages.eservice_creation.component.AdditionalInformationStepComponent;
import it.pagopa.interop.web.pages.eservice_creation.component.GeneralInformationStepComponent;
import it.pagopa.interop.web.pages.eservice_creation.component.TechnicalSpecificationStepComponent;

import static it.pagopa.interop.web.pages.eservice_creation.component.AdditionalInformationStepComponent.AdditionalInformationStepSeed;
import static it.pagopa.interop.web.pages.eservice_creation.component.TechnicalSpecificationStepComponent.TechnicalSpecificationStepSeed;

@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreationPage extends Page {

    /**
     * Tempo di wait tra la compilazione di uno step e l'altro, gestisce il problema
     * di eventual consistency di Interop dovuto a chiamate troppo ravvicinate
     */
    int WAIT_TIME = 2;

    @XPath(".//h1")
    Readable<String> title();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    @XPath(".//button[contains(., 'Vai al riepilogo')]")
    Button summeryButton();

    @XPath(".//button[contains(., 'Pubblica')]")
    Button publishButton();

    GeneralInformationStepComponent generalInformationStep();

    TechnicalSpecificationStepComponent technicalSpecificationStep();

    AdditionalInformationStepComponent additionalInformationStep();

    default EServiceCreationPage fillGeneralInformation(EServiceSeed seed) {
        generalInformationStep().fillGeneralInformation(seed);
        saveDraftButton().click();
        DelayUtils.waitForSeconds(WAIT_TIME);
        return this;
    }

    default EServiceCreationPage fillTechnicalSpecification(TechnicalSpecificationStepSeed seed) {
        technicalSpecificationStep().assertLoaded();
        technicalSpecificationStep().fillTechnicalSpecification(seed);
        DelayUtils.waitForSeconds(WAIT_TIME);
        saveDraftButton().click();
        return this;
    }

    default EServiceCreationPage fillAdditionalInformation(AdditionalInformationStepSeed seed) {
        additionalInformationStep().assertLoaded();
        additionalInformationStep().fillAdditionalInformation(seed);
        DelayUtils.waitForSeconds(WAIT_TIME);
        summeryButton().click();
        return this;
    }

    default EServiceCreationPage skipThresholdAndAttribute() {
        //TODO: assert loaded
        saveDraftButton().click();
        return this;
    }

    default EServiceCreationPage skipTechnicalSpecification() {
        technicalSpecificationStep().assertLoaded();
        saveDraftButton().click();
        return this;
    }

    default EServiceCreationPage skipAdditionalInformation() {
        additionalInformationStep().assertLoaded();
        summeryButton().click();
        return this;
    }

    default EServiceCreationPage publish() {
        publishButton().click();
        return this;
    }

    @Override
    default void assertLoaded() {
        generalInformationStep().assertLoaded();
        title().readAndAssert("Crea e-service");
    }
}

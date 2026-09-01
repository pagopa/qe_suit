package it.pagopa.interop.web.eservice.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.eservice.infrastructure.page.component.catalog.EServiceCatalogPage;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.GeneralDataWizard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.ThresholdAndAttributeWizard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.technical.TechnicalSpecWizard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.provision.EServiceProvisionCatalogPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class WebEServiceConfig {

    @Bean
    @ScenarioScope
    public GeneralDataWizard generalDataStepComponent(EServiceCreationPage creationPage) {
        return creationPage.generalDataStep();
    }

    @Bean
    @ScenarioScope
    public ThresholdAndAttributeWizard thresholdAndAttributeStep(EServiceCreationPage creationPage) {
        return creationPage.thresholdAndAttributeStep();
    }

    @Bean
    @ScenarioScope
    public TechnicalSpecWizard technicalDataStepComponent(EServiceCreationPage creationPage) {
        return creationPage.technicalSpecificationStep();
    }

    @Bean
    @ScenarioScope
    public EServiceCatalogPage eServiceCatalogPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceCatalogPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceCreationPage eServiceCreationPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceCreationPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceProvisionCatalogPage eServiceProvisionCatalogPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceProvisionCatalogPage.class);
    }

}

package it.pagopa.interop.ui.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.ui.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.page.eservice_creation.step.GeneralDataStep;
import it.pagopa.interop.ui.page.eservice_creation.step.ThresholdAndAttributeStep;
import it.pagopa.interop.ui.page.eservice_creation.step.technical.TechnicalSpecStep;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Getter
@Setter
public class ComponentConfig {

    @Bean
    @ScenarioScope
    public GeneralDataStep generalDataStepComponent(EServiceCreationPage creationPage) {
        return creationPage.generalDataStep();
    }

    @Bean
    @ScenarioScope
    public ThresholdAndAttributeStep thresholdAndAttributeStep(EServiceCreationPage creationPage) {
        return creationPage.thresholdAndAttributeStep();
    }

    @Bean
    @ScenarioScope
    public TechnicalSpecStep technicalDataStepComponent(EServiceCreationPage creationPage) {
        return creationPage.technicalSpecificationStep();
    }

}

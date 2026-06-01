package it.pagopa.interop.ui.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.GeneralDataStepComponent;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.ThresholdAndAttributeStep;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
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
    public GeneralDataStepComponent generalDataStepComponent(EServiceCreationPage creationPage) {
        return creationPage.generalDataStep();
    }

    @Bean
    @ScenarioScope
    public ThresholdAndAttributeStep thresholdAndAttributeStep(EServiceCreationPage creationPage) {
        return creationPage.thresholdAndAttributeStep();
    }

    @Bean
    @ScenarioScope
    public TechnicalSpecificationStepComponent technicalDataStepComponent(EServiceCreationPage creationPage) {
        return creationPage.technicalSpecificationStep();
    }

}

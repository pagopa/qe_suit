package it.pagopa.interop.web.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.web.eservice.creation.EServiceCreationPage;
import it.pagopa.interop.web.eservice.creation.wizard.GeneralDataWizard;
import it.pagopa.interop.web.eservice.creation.wizard.ThresholdAndAttributeWizard;
import it.pagopa.interop.web.eservice.creation.wizard.technical.TechnicalSpecWizard;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Getter
@Setter
public class WebComponentConfig {

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

}

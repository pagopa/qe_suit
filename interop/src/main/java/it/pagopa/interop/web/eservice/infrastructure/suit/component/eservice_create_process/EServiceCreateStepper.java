package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Stepper;

@XPath(".//*[@data-testid='mobileStepper']/parent::div")
public interface EServiceCreateStepper extends Stepper {
    @XPath(".//*[@data-testid='mobileStepperLabel']")
    Label label();

    @XPath(".//*[@data-testid='mobileStepperActive']")
    Label activeStep();

    @XPath(".//*[@data-testid='mobileStepperActive']/following-sibling::span[1]")
    Label activeTitle();
}

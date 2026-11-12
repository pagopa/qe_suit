package it.pagopa.interop.web.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.web.infrastructure.suit.component.UnauthorizedComponent;
import it.pagopa.interop.web.purpose_template.infrastructure.suite.page.ConsumerMyPurposeTemplatePage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebMyPurposeTemplateSteps {

    private final ConsumerMyPurposeTemplatePage consumerMyPurposeTemplatePage;
    private final UnauthorizedComponent unauthorizedComponent;

    @When("apre i miei template finalità tramite URL diretto")
    public void openMyPurposeTemplatePage() {
        consumerMyPurposeTemplatePage.navigateTo();
    }

    @Then("l'accesso alla pagina dei miei template finalità è {word}")
    public void checkMyPurposeTemplatePageLoaded(String accessResult) {
        boolean shouldBeLoaded = accessResult.equals("consentito");

        if (shouldBeLoaded) consumerMyPurposeTemplatePage.assertLoaded();
        else unauthorizedComponent.assertLoaded();
    }
}

package it.pagopa.interop.web.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.web.infrastructure.suit.component.UnauthorizedComponent;
import it.pagopa.interop.web.purpose_template.infrastructure.suite.page.ConsumerPurposeTemplateCatalogPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurposeTemplateCatalogSteps {

    private final ConsumerPurposeTemplateCatalogPage consumerPurposeTemplateCatalogPage;
    private final UnauthorizedComponent unauthorizedComponent;

    @When("apre il catalogo dei template finalità tramite URL diretto")
    public void openPurposeTemplateCatalogPage() {
        consumerPurposeTemplateCatalogPage.navigateTo();
    }

    @Then("l'accesso alla pagine del catalogo dei template finalità è {word}")
    public void checkPurposeTemplateCatalogPageLoaded(String accessResult) {
        boolean shouldBeLoaded = accessResult.equals("consentito");

        if (shouldBeLoaded) consumerPurposeTemplateCatalogPage.assertLoaded();
        else unauthorizedComponent.assertLoaded();
    }
}

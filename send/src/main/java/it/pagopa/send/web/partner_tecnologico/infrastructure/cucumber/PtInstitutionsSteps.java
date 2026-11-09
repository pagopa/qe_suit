package it.pagopa.send.web.partner_tecnologico.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.PtInstitutionsPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PtInstitutionsSteps {

    private final WebPresentationGateway uiGateway;

    @When("il PT cerca gli enti per {string}")
    public void searchInstitutions(String text) {
        uiGateway.bind(PtInstitutionsPage.class).searchInstitutions(text);
    }

    @Then("il filtro degli enti è vuoto")
    public void assertSearchIsEmpty() {
        uiGateway.bind(PtInstitutionsPage.class).searchInput()
                .readAndAssert(h -> Assertions.assertThat(h).isEmpty());
    }
}

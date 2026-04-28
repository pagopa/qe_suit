package it.pagopa.send.steps;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.steps.mittenti.DashboardPage;

public class DashboardPageSteps {
    private final DashboardPage currentPage;

    public DashboardPageSteps(WebPresentationGateway uiGateway) {
        this.currentPage = uiGateway.bind(DashboardPage.class);
    }

    @When("viene aperto il dettaglio di una notifica")
    public void openNotificationDetail() {
        currentPage.goToNotificationDetails();
    }
}

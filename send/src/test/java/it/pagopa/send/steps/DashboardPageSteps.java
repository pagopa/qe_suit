package it.pagopa.send.steps;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.steps.mittenti.Dashboard;

public class DashboardPageSteps {
    private final Dashboard currentPage;

    public DashboardPageSteps(WebPresentationGateway uiGateway) {
        this.currentPage = uiGateway.bind(Dashboard.class);
    }

    @When("viene aperto il dettaglio di una notifica")
    public void openNotificationDetail() {
        currentPage.goToNotificationDetails();
    }
}

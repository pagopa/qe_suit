package it.pagopa.send.web.notification_details.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.send.domain.web.pages.mittente.MittenteNotificationDetailsPage;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationStatusDetailsPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MittenteNotificationDetailsSteps {
    private final MittenteNotificationDetailsPage mittenteNotificationDetailsPage;
    private final NotificationStatusDetailsPage notificationStatusDetailsPage;
    private final WebBrowserContext webBrowserContext;

    @And("viene aperta la Sidebar contenente i dettagli aggiuntivi della notifica")
    public void openNotificationSidebar() {
        mittenteNotificationDetailsPage.notificationSummarySection().openDetailsSidebarButton().click();
    }

    @Then("il pannello dei dettagli aggiuntivi della notifica contiene tutti i campi popolati")
    public void assertNotificationStatusDrawerLoaded() {
        mittenteNotificationDetailsPage.notificationSummarySection().notificationStatusDrawer().assertLoaded();
    }

    @And("chiude il pannello dei dettagli aggiuntivi della notifica")
    public void closeNotificationStatusDrawer() {
        mittenteNotificationDetailsPage.notificationSummarySection().notificationStatusDrawer().close();
    }

    @And("visualizza il dettaglio dello stato della notifica")
    public void openNotificationStatusDetails() {
        mittenteNotificationDetailsPage.notificationStatusSection().details().click();
        notificationStatusDetailsPage.assertLoaded();

        webBrowserContext.setPreviousPage(mittenteNotificationDetailsPage);
        webBrowserContext.setCurrentPage(notificationStatusDetailsPage);
    }
}

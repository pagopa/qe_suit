package it.pagopa.send.controller;

import io.cucumber.java.en.When;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DashboardPageSteps {
    private final NotificationDetailsProxy notificationDetailsProxy;

    @When("viene aperto il dettaglio di una notifica")
    public void openNotificationDetail() {
        notificationDetailsProxy.goToNotificationDetails();
    }

}

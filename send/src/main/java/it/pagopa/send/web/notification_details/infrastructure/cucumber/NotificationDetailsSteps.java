package it.pagopa.send.web.notification_details.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.frontend.e2e.framework.web.domain.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationDetailsSteps {

    @Then("la pagina di dettaglio della notifica contiene la sezione relativa al {notificationDetailsSection}")
    @Then("la pagina di dettaglio della notifica contiene la sezione relativa ai {notificationDetailsSection}")
    public void assertNotificationDetailsSection(Component section) {
        section.assertLoaded();
    }

    @Then("la pagina di dettaglio della notifica non contiene la sezione relativa al {notificationDetailsSection}")
    @Then("la pagina di dettaglio della notifica non contiene la sezione relativa ai {notificationDetailsSection}")
    public void assertNotificationDetailsSectionNotPresent(Component section) {
        try {
            section.assertLoaded();
        } catch (AssertionError e) {
            // Se viene lanciata un'eccezione, significa che la sezione non è presente, quindi il test passa
            return;
        }
        throw new AssertionError("La sezione " + section.getClass().getSimpleName() +
                " è presente nella pagina di dettaglio della notifica, ma non dovrebbe esserlo.");
    }
}

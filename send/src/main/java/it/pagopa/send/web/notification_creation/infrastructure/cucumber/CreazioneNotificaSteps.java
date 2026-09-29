package it.pagopa.send.web.notification_creation.infrastructure.cucumber;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.web.notification_creation.infrastructure.factory.NotificationFactory;
import it.pagopa.send.web.notification_creation.application.NotificationData;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import it.pagopa.send.web.mittente.infrastructure.page.NotificationSuccessPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreazioneNotificaSteps {
    private final NotificationContext context;
    private final NotificationFactory notificationFactory;
    private final WebPresentationGateway browser;

    @When("crea e invia una notifica di tipo {string}")
    public void createsAndSendsNotification(String tipoNotifica) {
        // Load template
        NotificationData notification = notificationFactory.load(tipoNotifica);
        context.setNotifica(notification);
        
        // Navigate to page
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);

        // Fill form
        // TODO: page.fillAndSubmit(data)
        page.compileInformazioniPreliminari(notification);
        browser.click(page.informazioniPreliminariStep().groupOptionSelector(notification.getGroup()));
        page.continueButton().click();

        page.compileDestinatari(notification);
        page.continueButton().click();

        page.compileDettaglioPosizioneDebitoria(notification);
        page.continueButton().click();

        page.compileDocumentazione(notification);
        page.continueButton().click();
    }

    @Then("la notifica è stata inviata con successo")
    public void assertNotificationSentSuccessfully() {
        NotificationSuccessPage successPage = browser.bind(NotificationSuccessPage.class);
        successPage.assertLoaded();
    }
}

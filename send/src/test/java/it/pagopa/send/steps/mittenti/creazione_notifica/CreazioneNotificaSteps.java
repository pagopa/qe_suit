package it.pagopa.send.steps.mittenti.creazione_notifica;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.factory.NotificationFactory;
import it.pagopa.send.model.NotificationData;
import it.pagopa.send.steps.mittenti.pages.CreateNotificationPage;
import it.pagopa.send.steps.mittenti.pages.NotificationSuccessPage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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

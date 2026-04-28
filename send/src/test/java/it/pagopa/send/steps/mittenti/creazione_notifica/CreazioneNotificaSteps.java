package it.pagopa.send.steps.mittenti.creazione_notifica;

import io.cucumber.java.en.Given;
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
    private final NotificationContext context = new NotificationContext();
    private final NotificationFactory notificationFactory;
    private final WebPresentationGateway browser;

    @Given("una notifica di tipo {string}")
    public void loadNotificationTemplate(String templateName) {
        NotificationData notification = notificationFactory.load(templateName);
        context.setNotifica(notification);
    }

    @When("compila il form con i dati della notifica")
    public void fillNotificationForm() {
        NotificationData data = context.getNotifica();
        CreateNotificationPage page = browser.bind(CreateNotificationPage.class);
        // TODO: page.fillAndSubmit(data)
        page.compileInformazioniPreliminari(data);
        browser.click(page.informazioniPreliminariStep().groupOptionSelector(data.getGroup()));

        page.continueButton().click();

        page.compileDestinatari(data);
        page.continueButton().click();

        page.compileDettaglioPosizioneDebitoria(data);
        page.continueButton().click();

        page.compileDocumentazione(data);
        page.continueButton().click();
    }

    @Then("la notifica è stata inviata con successo")
    public void assertNotificationSentSuccessfully() {
        NotificationSuccessPage successPage = browser.bind(NotificationSuccessPage.class);
        successPage.assertLoaded();
    }
}

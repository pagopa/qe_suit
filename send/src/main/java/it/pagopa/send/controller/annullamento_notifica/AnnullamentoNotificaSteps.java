package it.pagopa.send.controller.annullamento_notifica;

import io.cucumber.java.en.When;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.legalnotification.application.LegalNotificationJourney;
import it.pagopa.send.utils.IUNHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnullamentoNotificaSteps {
    private final LegalNotificationJourney journey;

    @When("l'ente annulla la notifica {notification}")
    public void annullamentNotifica(BffNewNotificationResponse notification) {
        journey.withIUN(IUNHelper.extractFromBffNewNotificationResponse(notification))
                .delete();
        log.info("Request di annullamento notifica inviata");
    }
}

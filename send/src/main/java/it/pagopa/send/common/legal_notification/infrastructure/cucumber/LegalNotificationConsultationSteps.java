package it.pagopa.send.common.legal_notification.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.Map;

/**
 * Consultazione, da parte della PA mittente, dell'ultima notifica creata nello scenario: ricerca tra le notifiche
 * inviate e cronologia degli stati. La notifica trovata nel When resta nell'istanza degli step, che Cucumber crea per
 * ogni scenario.
 */
@RequiredArgsConstructor
public class LegalNotificationConsultationSteps {

    private final LegalNotificationUseCase legalNotificationUseCase;
    private final CurrentUserSession currentUserSession;

    private LegalNotificationDomain foundNotification;

    @When("la PA {user} cerca {legalNotification} per IUN")
    public void searchByIun(User sender, LegalNotificationDomain notification) {
        find(sender, notification, Map.of("iun", notification.getIun()));
    }

    /** La notifica è stata inviata nello scenario: la data di invio è quella odierna. */
    @When("la PA {user} cerca {legalNotification} per data di invio")
    public void searchBySentDate(User sender, LegalNotificationDomain notification) {
        String today = LocalDate.now().toString();
        find(sender, notification, Map.of("startDate", today, "endDate", today));
    }

    @Then("{legalNotification} compare tra i risultati della ricerca con il suo stato attuale")
    public void assertNotificationFound(LegalNotificationDomain notification) {
        Assertions.assertThat(foundNotification.getIun())
                .as("Notifica trovata tra i risultati della ricerca")
                .isEqualTo(notification.getIun());
        Assertions.assertThat(foundNotification.getStatus())
                .as("Stato della notifica %s tra i risultati della ricerca", notification.getIun())
                .isNotNull();
    }

    /**
     * La cronologia è letta dal dettaglio della notifica ("Stato della notifica"): lo stato mostrato tra i risultati
     * della ricerca deve essere tra quelli attraversati. Non si confronta con lo stato più recente perché l'elenco
     * delle notifiche si aggiorna in ritardo rispetto al dettaglio: una notifica in lavorazione può risultare
     * "Depositata" nell'elenco e già "Invio in corso" nella cronologia.
     */
    @Then("la cronologia della notifica è coerente con lo stato attuale")
    public void assertTimelineMatchesCurrentStatus() {
        // cronologia dal dettaglio della notifica, da confrontare con lo stato letto nella ricerca
        NotificationTimeline timeline = legalNotificationUseCase.readNotificationTimeline(foundNotification.getIun());
        Assertions.assertThat(timeline.statuses())
                .as("Stati della cronologia della notifica %s rispetto alla ricerca", timeline.iun())
                .contains(foundNotification.getStatus());
    }

    // una notifica appena accettata compare tra quelle della PA con qualche secondo di ritardo: la ricerca si ripete
    private void find(User sender, LegalNotificationDomain notification, Map<String, String> filters) {
        currentUserSession.setUser(sender);
        foundNotification = legalNotificationUseCase.findNotification(notification.getIun(), filters);
    }
}

package it.pagopa.send.common.legal_notification.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.application.LegalNotificationUseCase;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationDocumentType;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.NotificationTimeline;
import it.pagopa.send.common.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Consultazione, da parte della PA mittente, dell'ultima notifica creata nello scenario: ricerca tra le notifiche
 * inviate, cronologia degli stati e download dei documenti. Quanto letto o scaricato nei When resta nell'istanza degli
 * step, che Cucumber crea per ogni scenario.
 */
@RequiredArgsConstructor
public class LegalNotificationConsultationSteps {

    private final LegalNotificationUseCase legalNotificationUseCase;
    private final CurrentUserSession currentUserSession;

    private LegalNotificationDomain foundNotification;
    private NotificationTimeline timeline;
    private final List<String> downloadedFiles = new ArrayList<>();

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

    @When("la PA {user} consulta la cronologia {legalNotification}")
    public void readTimeline(User sender, LegalNotificationDomain notification) {
        currentUserSession.setUser(sender);
        timeline = legalNotificationUseCase.readNotificationTimeline(notification.getIun());
    }

    /** Ripetibile: ogni documento scaricato si aggiunge a quelli dello scenario. */
    @When("la PA {user} scarica {notificationDocument} {legalNotification}")
    public void downloadDocument(User sender, NotificationDocumentType type, LegalNotificationDomain notification) {
        currentUserSession.setUser(sender);
        downloadedFiles.add(legalNotificationUseCase.downloadDocument(notification.getIun(), type));
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
        NotificationTimeline detailTimeline = legalNotificationUseCase.readNotificationTimeline(foundNotification.getIun());
        Assertions.assertThat(detailTimeline.statuses())
                .as("Stati della cronologia della notifica %s rispetto alla ricerca", detailTimeline.iun())
                .contains(foundNotification.getStatus());
    }

    /**
     * La data da cui la notifica è perfezionata è riportata. È una sola per l'intera notifica, anche con più
     * destinatari: il portale della PA non mostra il perfezionamento di ciascun destinatario.
     */
    @Then("la cronologia riporta la data di perfezionamento della notifica")
    public void assertRefinementDate() {
        NotificationTimeline.StatusChange refinement = timeline.find(NotificationStatus.EFFECTIVE_DATE)
                .orElseThrow(() -> new AssertionError("Stato di perfezionamento assente nella cronologia della notifica "
                        + timeline.iun() + ": " + timeline.statuses()));
        Assertions.assertThat(refinement.activeFrom())
                .as("Data di perfezionamento della notifica %s", timeline.iun())
                .isNotNull();
    }

    /** I documenti scaricati nello scenario sono file PDF. */
    @Then("i documenti vengono scaricati")
    public void assertDocumentsDownloaded() {
        Assertions.assertThat(downloadedFiles)
                .as("File dei documenti scaricati")
                .isNotEmpty()
                .allSatisfy(file -> Assertions.assertThat(file).endsWithIgnoringCase(".pdf"));
    }

    // una notifica appena accettata compare tra quelle della PA con qualche secondo di ritardo: la ricerca si ripete
    private void find(User sender, LegalNotificationDomain notification, Map<String, String> filters) {
        currentUserSession.setUser(sender);
        foundNotification = legalNotificationUseCase.findNotification(notification.getIun(), filters);
    }
}

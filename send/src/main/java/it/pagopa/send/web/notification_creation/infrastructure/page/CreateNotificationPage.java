package it.pagopa.send.web.notification_creation.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.infrastructure.page.component.ErrorSnackbar;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_creation.application.NotificationData;
import it.pagopa.send.web.notification_creation.infrastructure.page.component.DestinatariStep;
import it.pagopa.send.web.notification_creation.infrastructure.page.component.DettaglioPosizioneDebitoriaStep;
import it.pagopa.send.web.notification_creation.infrastructure.page.component.DocumentazioneStep;
import it.pagopa.send.web.notification_creation.infrastructure.page.component.ExitDialog;
import it.pagopa.send.web.notification_creation.infrastructure.page.component.InformazioniPreliminariStep;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * {@code {baseUrl}/nuova-notifica}
 * Pagina "Invia una nuova notifica" della PA: un wizard con i passi "Informazioni preliminari", "Destinatari",
 * "Posizione debitoria", "Dettaglio posizione debitoria" e "Documentazione".
 * Si apre dal pulsante "Invia una nuova notifica" della dashboard; uscire dalla pagina verso un'altra pagina del portale
 * apre la richiesta di conferma "Vuoi uscire?".
 * I testi del passo corrente si leggono con gli elementi comuni al form (titolo, sezioni, etichette, opzioni, pulsanti);
 * testi e stato iniziale dei passi sono verificati da {@code WebNewNotificationPaContractTest}.
 */
@Url("${url.notifiche.mittente.new-notification}")
public interface CreateNotificationPage extends Page, CreateNotificationForm {

    // ultima voce delle breadcrumb ("Notifiche > Nuova notifica"): l'id "title-of-page" non esiste più nel portale
    @XPath("(//nav[@aria-label=\"breadcrumbs\"]//li[contains(@class, \"MuiBreadcrumbs-li\")])[last()]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid='step-submit']")
    Clickable continueButton();

    // il banner dei cookie copre i campi del form: va accettato prima di compilarlo
    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    InformazioniPreliminariStep informazioniPreliminariStep();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DestinatariStep destinatariStep();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DettaglioPosizioneDebitoriaStep dettaglioPosizioneDebitoriaStep();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DocumentazioneStep documentazioneStep();

    // intestazione e passi del wizard

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Button notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"api-how-it-works\"]")
    Readable<String> howItWorksLink();

    @XPath("//*[@data-testid=\"stepper\"]//*[contains(@class, 'MuiStepLabel-labelContainer')]")
    Readable<String> stepNames();

    // uscita dalla pagina

    @XPath("//*[@data-testid=\"promptDialog\"]")
    ExitDialog exitDialog();

    // dashboard aperta confermando l'uscita
    @XPath("//main")
    DashboardPage dashboard();

    /**
     * Legge i testi di più elementi del passo. La lettura di più elementi non attende che siano visibili e, finché il
     * passo non è del tutto visibile, restituisce testi vuoti: la lettura si ripete finché nessun testo è vuoto.
     */
    default List<String> visibleTexts(Readable<String> texts) {
        return PollingUtils.pollUntil(
                texts::readAll,
                values -> !values.isEmpty() && values.stream().noneMatch(String::isEmpty),
                Duration.ofSeconds(10),
                Duration.ofSeconds(1),
                "Testi del passo non visibili");
    }

    // i passi dopo il primo si raggiungono compilando i precedenti con dati validi: la notifica parte solo con "Invia"

    default void goToRecipientsStep(NotificationData data) {
        compileInformazioniPreliminari(data);
        continueToNextStep();
    }

    default void goToDebtPositionStep(NotificationData data) {
        goToRecipientsStep(data);
        compileDestinatari(data);
        continueToNextStep();
    }

    default void goToDebtPositionDetailStep(NotificationData data) {
        goToDebtPositionStep(data);
        pagoPaRadio().select();
        continueToNextStep();
    }

    default void goToDocumentsStep(NotificationData data) {
        goToDebtPositionStep(data);
        noPaymentRadio().select();
        continueToNextStep();
    }

    Optional<ErrorSnackbar> errorSnackbar();

    /**
     * "Continua" dopo aver chiuso il messaggio d'errore del portale, se compare, perché copre il pulsante: l'ambiente
     * test risponde con {@code PN_EXTERNALREGISTRIES_INSTITUTIONSERROR} durante la compilazione dei passi e il
     * messaggio intercetta il clic.
     * TODO: rimuovere la chiusura del messaggio quando l'errore non si presenterà più, perché nasconde un errore reale
     * del portale.
     */
    default void continueToNextStep() {
        errorSnackbar().ifPresent(ErrorSnackbar::closeIfShown);
        continueButton().click();
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Nuova notifica", "New notification");
        });
    }

    default void compileInformazioniPreliminari(NotificationData data) {
       informazioniPreliminariStep().fillFields(data);
    }

    default void compileDestinatari(NotificationData data) {
        destinatariStep().fillFields(data);
    }

    default void compileDettaglioPosizioneDebitoria(NotificationData data) {
        dettaglioPosizioneDebitoriaStep().fillFields(data);
    }

    default void compileDocumentazione(NotificationData data) {
        documentazioneStep().fillFields(data);
    }
}

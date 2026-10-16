package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.notification_search.infrastructure.suit.RecipientNotificationsPage;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche}
 * Pagina "In arrivo per &lt;impresa&gt;" della persona giuridica, pagina iniziale del portale dopo il login.
 * Si apre dalla voce "In arrivo per &lt;impresa&gt;" del menu laterale "In arrivo".
 * È la stessa pagina "In arrivo" del cittadino, con il nome dell'impresa nel titolo: filtri, tabella e banner sono
 * definiti in {@link RecipientNotificationsPage}.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, l'unico elemento presente per tutti gli utenti:
 * filtri, tabella e banner dipendono dalle notifiche ricevute, dai recapiti e dal ruolo. È usato anche dal login degli
 * step Cucumber. Il titolo completo è verificato da {@code WebNotificationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche}")
public interface NotificationPage extends RecipientNotificationsPage {

    // il titolo contiene il nome dell'impresa, per esempio "In arrivo per Le Epistolae srl"
    String TITLE_PREFIX = "In arrivo per ";

    String OPEN_BUTTON_TEST_ID = "goToNotificationDetail";
    long LEGAL_NOTIFICATION_WAIT_MILLIS = 15000; //15s
    long LEGAL_NOTIFICATION_POLL_MILLIS = 250; //0.25s
    long NEW_NOTIFICATION_WAIT_MILLIS = 180000; //3min
    long NEW_NOTIFICATION_POLL_MILLIS = 5000; //5s

    // riga di una notifica a valore legale: le comunicazioni aprono un'altra pagina
    String LEGAL_NOTIFICATION_ROW = "//*[@data-testid=\"notificationsTable.body.row\"]"
            + "[.//span[normalize-space()=\"Notifica a valore legale\"]]";

    // "Apri" dell'ultima notifica a valore legale della lista
    @XPath("(" + LEGAL_NOTIFICATION_ROW + "//*[@data-testid=\"" + OPEN_BUTTON_TEST_ID + "\"])[last()]")
    Button lastLegalNotificationOpenButton();

    // "Apri" della prima notifica a valore legale della lista, la più recente
    @XPath("(" + LEGAL_NOTIFICATION_ROW + "//*[@data-testid=\"" + OPEN_BUTTON_TEST_ID + "\"])[1]")
    Button firstLegalNotificationOpenButton();

    // "Apri" se c'è una notifica a valore legale, altrimenti il main della lista caricata (tabella o stato vuoto) senza
    // notifiche a valore legale: la ricerca attende il caricamento e trova sempre un elemento, senza il timeout del
    // framework (60 s) quando la notifica manca
    @XPath("(" + LEGAL_NOTIFICATION_ROW + "//*[@data-testid=\"" + OPEN_BUTTON_TEST_ID + "\"])[last()]"
            + " | //main[(.//*[@data-testid=\"notificationsTable\"] or .//*[@data-testid=\"emptyState\"])"
            + " and not(" + LEGAL_NOTIFICATION_ROW + ")]")
    Button lastLegalNotificationOpenButtonOrList();

    // dettaglio aperto con "Apri"
    @XPath("//main")
    NotificationDetailPGPage notificationDetail();

    /**
     * Indica se nella pagina corrente della lista c'è almeno una notifica a valore legale. Appena caricata, la lista può
     * risultare per un istante senza righe prima dell'arrivo delle notifiche: se il pulsante "Apri" non c'è, lo si
     * attende per al massimo {@value #LEGAL_NOTIFICATION_WAIT_MILLIS} ms, invece dei 60 secondi della ricerca di un
     * elemento assente.
     */
    default boolean hasLegalNotification() {
        long deadline = System.currentTimeMillis() + LEGAL_NOTIFICATION_WAIT_MILLIS;
        while (true) {
            boolean found = lastLegalNotificationOpenButtonOrList().get(FindPolicy.PRESENT)
                    .map(element -> OPEN_BUTTON_TEST_ID.equals(element.getAttributes().get("data-testid")))
                    .orElse(false);
            if (found || System.currentTimeMillis() >= deadline) {
                return found;
            }
            try {
                Thread.sleep(LEGAL_NOTIFICATION_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * Apre la notifica a valore legale appena inviata con lo IUN indicato: è la più recente, quindi la prima della lista.
     * Dopo l'accettazione può comparire nella lista con qualche istante di ritardo: finché la prima notifica a valore
     * legale non è quella cercata (il pulsante "Apri" riporta lo IUN nell'aria-label) la lista viene ricaricata, ogni
     * {@value #NEW_NOTIFICATION_POLL_MILLIS} ms e per al massimo {@value #NEW_NOTIFICATION_WAIT_MILLIS} ms.
     */
    default void openNewLegalNotification(String iun) {
        long deadline = System.currentTimeMillis() + NEW_NOTIFICATION_WAIT_MILLIS;
        while (true) {
            navigateTo();
            boolean shown = hasLegalNotification() && firstLegalNotificationOpenButton().get(FindPolicy.PRESENT)
                    .map(element -> element.getAttributes().get("aria-label"))
                    .map(label -> label.endsWith(iun))
                    .orElse(false);
            if (shown) {
                firstLegalNotificationOpenButton().click();
                return;
            }
            if (System.currentTimeMillis() >= deadline) {
                throw new IllegalStateException("La notifica " + iun + " non compare nella lista dopo "
                        + NEW_NOTIFICATION_WAIT_MILLIS / 1000 + " s");
            }
            try {
                Thread.sleep(NEW_NOTIFICATION_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Attesa della notifica " + iun + " interrotta", e);
            }
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).startsWith(TITLE_PREFIX));
    }
}

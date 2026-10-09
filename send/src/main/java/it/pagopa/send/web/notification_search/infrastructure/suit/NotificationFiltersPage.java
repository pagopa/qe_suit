package it.pagopa.send.web.notification_search.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;

import java.util.Optional;

/**
 * Elementi comuni alle liste di notifiche dei destinatari: titolo, filtri per IUN e date con i loro messaggi, contenuto
 * della pagina e opzioni di "Righe per pagina". Li hanno "In arrivo" del cittadino e dell'impresa
 * ({@link RecipientNotificationsPage}) e "In arrivo dalle deleghe" dell'impresa, che non ha il filtro "Tipologia" e ha
 * una tabella con la colonna del destinatario.
 * <p>
 * L'{@link Url} non è risolta: questa interfaccia non viene mai bindata direttamente, solo le sue implementazioni
 * concrete lo sono, ciascuna con la propria URL reale.
 */
@Url("about:blank")
public interface NotificationFiltersPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"iunMatch-label\"]")
    Readable<String> iunSearchLabel();

    @XPath("//*[@id=\"iunMatch\"]")
    TextField iunSearchInput();

    @XPath("//*[@id=\"startDate-label\"]")
    Readable<String> startDateSearchLabel();

    @XPath("//*[@id=\"startDate\"]")
    TextField startDateSearchInput();

    @XPath("//*[@id=\"endDate-label\"]")
    Readable<String> endDateSearchLabel();

    @XPath("//*[@id=\"endDate\"]")
    TextField endDateSearchInput();

    @XPath("//*[@id=\"filter-notifications-button\"]")
    Button filterButton();

    @XPath("//*[@id=\"iunMatch-helper-text\"]")
    Readable<String> iunErrorMessage();

    @XPath("//*[@id=\"startDate-helper-text\"]")
    Readable<String> startDateErrorMessage();

    @XPath("//*[@id=\"endDate-helper-text\"]")
    Readable<String> endDateErrorMessage();

    // contenuto della pagina, per sapere se ci sono il banner e la tabella senza attenderli
    @XPath("//main")
    Readable<String> content();

    // opzioni del menu "Righe per pagina"
    @XPath("//*[@role=\"menu\"]//*[@role=\"menuitem\"]")
    Readable<String> rowsPerPageOptions();
}

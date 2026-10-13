package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/integrazione-api}
 * Pagina "Integrazione API" dell'impresa.
 * Si apre dalla voce "Integrazione API" del menu laterale.
 * Contiene due tabelle di chiavi, "Chiave pubblica" e "Chiave personale" ({@link KeysTable}); ogni chiave ha uno stato
 * (Attiva, Ruotata, Bloccata) e un menu ({@link KeyMenu}) con "Visualizza codice", che apre la finestra con i codici
 * ({@link ViewCodeDialog}), ed "Elimina" per le chiavi non attive, che nei test non va mai premuto. Un'impresa senza
 * chiavi vede solo la sezione "Chiave pubblica", con un messaggio e "Registra chiave pubblica" al posto della tabella;
 * il form di registrazione è in {@link PublicKeyRegistrationPage}.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e il titolo della sezione "Chiave pubblica"; testi,
 * tabelle, menu e finestre sono verificati da {@code WebApiIntegrationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.integrazione-api}")
public interface ApiIntegrationPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // contenuto della pagina, per sapere se ci sono le tabelle senza attenderle
    @XPath("//main")
    Readable<String> content();

    @XPath("//*[@data-testid=\"publicKeysTableDesktop\"]")
    KeysTable publicKeysTable();

    // senza chiavi pubbliche, al posto della tabella: messaggio e pulsante per registrarne una
    @XPath("//*[@data-testid=\"emptyState\"]")
    Readable<String> publicKeysEmptyState();

    @XPath("//*[@id=\"generate-public-key\"]")
    Button registerPublicKeyButton();

    @XPath("//*[@data-testid=\"virtualKeysTableDesktop\"]")
    KeysTable personalKeysTable();

    // menu aperto da "⋮" di una riga
    @XPath("//*[@role=\"menu\"]")
    KeyMenu keyMenu();

    @XPath("//*[@role=\"dialog\"][.//*[@data-testid=\"close-modal-button\"]]")
    ViewCodeDialog viewCodeDialog();

    // labels

    @XPath("//*[@data-testid=\"publicKeys\"]/h2")
    Readable<String> publicKeysTitle();

    @XPath("//*[@data-testid=\"virtualKeys\"]/h2")
    Readable<String> personalKeysTitle();

    /**
     * Tabella delle chiavi, uguale nelle due sezioni tranne la terza colonna (scadenza o ultima modifica). Gli elementi
     * delle colonne leggono tutte le righe; quelli di una chiave attiva, ruotata o bloccata leggono la prima riga con
     * quello stato.
     */
    interface KeysTable extends Component {
        @XPath(".//th")
        Readable<String> headers();

        @XPath(".//tbody/tr/td[1]")
        Readable<String> names();

        @XPath(".//tbody/tr/td[2]//p")
        Readable<String> values();

        @XPath(".//tbody/tr/td[3]")
        Readable<String> dates();

        @XPath(".//tbody/tr/td[4]")
        Readable<String> states();

        // "Copia" del valore, attivo solo per le chiavi attive
        @XPath(".//tbody/tr//*[@data-testid=\"copyToClipboard\"][not(@disabled)]")
        Readable<String> enabledCopyButtons();

        @XPath(".//tbody/tr[.//*[@data-testid=\"statusChip-Attiva\"]][1]/td[2]//p")
        Readable<String> activeKeyValue();

        // "Copia" attivo su una chiave non attiva: non deve esserci
        @XPath(".//tbody/tr[not(.//*[@data-testid=\"statusChip-Attiva\"])]//*[@data-testid=\"copyToClipboard\"][not(@disabled)]")
        Button inactiveKeyEnabledCopyButton();

        @XPath(".//tbody/tr[.//*[@data-testid=\"statusChip-Attiva\"]][1]//*[@data-testid=\"contextMenuButton\"]")
        Button activeKeyMenuButton();

        @XPath(".//tbody/tr[.//*[@data-testid=\"statusChip-Ruotata\"]][1]//*[@data-testid=\"contextMenuButton\"]")
        Button rotatedKeyMenuButton();

        @XPath(".//tbody/tr[.//*[@data-testid=\"statusChip-Bloccata\"]][1]//*[@data-testid=\"contextMenuButton\"]")
        Button blockedKeyMenuButton();

        @Override
        default void assertLoaded() {
            headers().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
        }
    }

    /**
     * Menu di una chiave: "Visualizza codice" per tutte, "Elimina" solo per quelle ruotate o bloccate.
     */
    interface KeyMenu extends Component {
        @XPath("//*[@role=\"menu\"]//*[@role=\"menuitem\"]")
        Readable<String> items();

        @XPath("//*[@role=\"menu\"]//*[@id=\"button-view\"]")
        Button viewCodeItem();

        @XPath("//*[@role=\"menu\"]//*[@id=\"button-delete\"]")
        Button deleteItem();

        @Override
        default void assertLoaded() {
            items().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
        }
    }

    /**
     * Finestra di "Visualizza codice", con i codici in sola lettura e un pulsante "Copia" per ciascuno: per la chiave
     * pubblica codice, Kid e Issuer, per la chiave personale solo il codice.
     */
    interface ViewCodeDialog extends Component {
        // anche la finestra dei cookie ha role="dialog": il titolo si cerca in quella con il pulsante di chiusura
        @XPath("//*[@role=\"dialog\"][.//*[@data-testid=\"close-modal-button\"]]//h2")
        Readable<String> title();

        @XPath("//*[@data-testid=\"subtitle-top\"]")
        Readable<String> subtitle();

        @XPath("//*[@data-testid=\"dialog-content\"]//label")
        Readable<String> fieldLabels();

        @XPath("//*[@data-testid=\"dialog-content\"]//input[@name=\"value\"]")
        TextField valueField();

        @XPath("//*[@data-testid=\"dialog-content\"]//input[@name=\"kid\"]")
        TextField kidField();

        @XPath("//*[@data-testid=\"dialog-content\"]//input[@name=\"issuer\"]")
        TextField issuerField();

        @XPath("//*[@data-testid=\"dialog-content\"]//button[@aria-label=\"Copia\"]")
        Readable<String> copyButtons();

        @XPath("//*[@data-testid=\"close-modal-button\"]")
        Button closeButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Integrazione API"));
        publicKeysTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}

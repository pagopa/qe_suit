package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/api-keys/nuova-api-key}
 * Pagina "Genera una API Key" della PA.
 * Si apre dal pulsante "Genera API Key" della pagina {@code {baseUrl}/api-keys}; è riservata ai ruoli admin e operatore.
 * Contiene il form con il nome della API Key e i gruppi a cui renderla visibile. Uscire dalla pagina verso un'altra pagina
 * del portale apre la richiesta di conferma "Vuoi uscire?".
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e la presenza del campo del nome e del pulsante "Continua";
 * testi e stato iniziale del form sono verificati da {@code WebNewApiKeyPaContractTest}.
 */
@Url("${url.notifiche.mittente.new-api-key}")
public interface NewAPIKeyPage extends Page {

    String TITLE = "Genera una API Key";

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Button apiKeysBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    @XPath("//*[@id=\"name\"]")
    TextField nameInput();

    @XPath("//*[@id=\"name-helper-text\"]")
    Readable<String> nameHelperText();

    @XPath("//*[@data-testid=\"submit-new-api-key\"]")
    Button continueButton();

    // il pulsante disabilitato non risulta visibile a Selenium: il testo si legge dal contenitore, che ha solo il pulsante
    @XPath("//*[@data-testid=\"submit-new-api-key\"]/..")
    Readable<String> continueButtonText();

    // testo dell'intero campo dei gruppi: l'etichetta e i gruppi selezionati
    @XPath("//*[@data-testid=\"groups\"]")
    Readable<String> groupsField();

    @XPath("//*[@data-testid=\"promptDialog\"]")
    ExitDialog exitDialog();

    // pagina "API Key" aperta confermando l'uscita
    @XPath("//main")
    APIKeyPage apiKeys();

    // labels

    @XPath("//*[@data-testid=\"new-api-key-form\"]/p[1]")
    Readable<String> requiredFieldsLabel();

    @XPath("//*[@data-testid=\"new-api-key-form\"]//*[@id=\"title-heading-section\"]")
    Readable<String> sectionTitle();

    @XPath("//*[@id=\"name\"]/preceding::p[1]")
    Readable<String> nameTitle();

    @XPath("//*[@id=\"name-label\"]")
    Readable<String> nameLabel();

    @XPath("//*[@data-testid=\"groups\"]/preceding::p[1]")
    Readable<String> groupsTitle();

    @XPath("//*[@data-testid=\"groups\"]//label")
    Readable<String> groupsLabel();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
        Assertions.assertThat(nameInput().get(FindPolicy.PRESENT)).isPresent();
        continueButton().assertLoaded();
    }

    /**
     * Richiesta di conferma "Vuoi uscire?", aperta quando si lascia la pagina verso un'altra pagina del portale:
     * "Annulla" resta sulla pagina, "Esci" prosegue verso la pagina scelta.
     */
    interface ExitDialog extends Component {
        @XPath("//*[@data-testid=\"promptDialog\"]//h2")
        Readable<String> title();

        @XPath("//*[@data-testid=\"promptDialog\"]//*[contains(@class, 'MuiDialogContentText-root')]")
        Readable<String> message();

        @XPath("//*[@data-testid=\"promptDialog\"]//button[not(@data-testid=\"confirmExitBtn\")]")
        Button cancelButton();

        @XPath("//*[@data-testid=\"confirmExitBtn\"]")
        Button exitButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }
}

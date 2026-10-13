package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/integrazione-api/registra-chiave-pubblica}
 * Pagina "Registra chiave pubblica" dell'impresa.
 * Si apre da "Registra chiave pubblica" della pagina "Integrazione API" ({@link ApiIntegrationPage}).
 * È una procedura in due passi, "Inserisci i dati" e "Ottieni parametri": il primo ha il form con il nome della chiave,
 * già compilato, e il valore della chiave pubblica, entrambi obbligatori; "Registra", che nei test non va mai premuto,
 * passa al secondo.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, il titolo del passo e "Registra"; testi
 * e messaggi di errore sono verificati da {@code WebPublicKeyRegistrationPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.integrazione-api-registra-chiave-pubblica}")
public interface PublicKeyRegistrationPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"exitBtn\"]")
    Button exitButton();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // nomi dei passi, senza il numero dell'icona
    @XPath("//*[@data-testid=\"stepper\"]//*[contains(concat(\" \", @class, \" \"), \" MuiStepLabel-label \")]")
    Readable<String> stepLabels();

    @XPath("//*[@data-testid=\"stepper\"]//*[contains(concat(\" \", @class, \" \"), \" MuiStepLabel-label \")][contains(@class, \"Mui-active\")]")
    Readable<String> currentStepLabel();

    // passo "Inserisci i dati"

    @XPath("//*[@data-testid=\"title\"]")
    Readable<String> stepTitle();

    @XPath("//*[@data-testid=\"title\"]/following-sibling::*[@data-testid=\"content\"]")
    Readable<String> stepDescription();

    // testi del form: indicazione del nome, del valore e righe di inizio e fine della chiave
    @XPath("//*[@data-testid=\"publicKeyDataInsertForm\"]//p[@data-testid=\"content\"]")
    Readable<String> formTexts();

    @XPath("//*[@id=\"name-label\"]")
    Readable<String> nameLabel();

    @XPath("//*[@id=\"name\"]")
    TextField nameInput();

    @XPath("//*[@id=\"name-helper-text\"]")
    Readable<String> nameError();

    @XPath("//*[@id=\"publicKey\"]")
    TextField publicKeyInput();

    @XPath("//*[@id=\"publicKey-helper-text\"]")
    Readable<String> publicKeyError();

    @XPath("//*[@id=\"step-submit\"]")
    Button registerButton();

    @XPath("//*[@id=\"previous-step\"]")
    Button backButton();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Registra chiave pubblica"));
        stepTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        // "Registra" resta disabilitato finché il form non è valido: si cerca solo che ci sia
        Assertions.assertThat(registerButton().get(FindPolicy.PRESENT)).isPresent();
    }
}

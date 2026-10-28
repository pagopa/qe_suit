package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;

/**
 * {@code {baseUrl}/api-keys}
 * Pagina "API Key" della PA: elenco delle API Key generate per l'ente e pulsante "Genera API Key".
 * Se l'ente non ha API Key, al posto della tabella compare un messaggio con il link per generarne una.
 * Testi e stato iniziale sono verificati da {@code WebApiKeysPaContractTest}.
 */
@Url("${url.notifiche.mittente.api-keys}")
public interface APIKeyPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@id=\"subtitle-page\"]//a")
    Readable<String> b2bApiLink();

    @XPath("//*[@data-testid=\"generateApiKey\"]/preceding-sibling::h2")
    Readable<String> generatedApiKeysTitle();

    @XPath("//*[@id=\"login-page-title\"]")
    Readable<String> supportHeader();

    @XPath("//*[@data-testid=\"generateApiKey\"]")
    Button generateApiKeyButton();

    // intestazioni con testo: l'ultima colonna, quella del menu azioni, non ha intestazione
    @XPath("//*[@data-testid=\"tableApiKeys\"]//th[normalize-space()]")
    Readable<String> tableHeaders();

    @XPath("//*[@data-testid=\"tableApiKeys.body.row\"]")
    List<ApiKeyRow> apiKeyRows();

    @XPath("//*[@data-testid=\"menuContext\"]//li[@role=\"menuitem\"]")
    Readable<String> contextMenuItems();

    // pagina "Genera una API Key" aperta con "Genera API Key"
    @XPath("//main")
    NewAPIKeyPage newApiKey();

    /**
     * Riga della tabella delle API Key. La colonna "API Key" contiene il valore della chiave, che è un segreto: non
     * viene letta.
     */
    interface ApiKeyRow extends Component {
        @XPath("./td[1]")
        Readable<String> name();

        @XPath("./td[5]")
        Readable<String> status();

        @XPath(".//*[@data-testid=\"contextMenuButton\"]")
        Button contextMenuButton();
    }

    /**
     * Riga dell'API Key con il nome indicato; la ricerca è limitata alla pagina della tabella mostrata.
     */
    default ApiKeyRow apiKeyRow(String name) {
        return apiKeyRows().stream()
                .filter(row -> name.equals(row.name().read()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nessuna API Key \"" + name + "\" nella tabella"));
    }

    @Override
    default void assertLoaded() {
       oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h).isIn("API Key", "API Key");
       });
    }

    default void assertSupportCannotSeeApiKey() {
        supportHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("You do not have the necessary authorizations to access this page",
                    "Non hai le autorizzazioni necessarie per accedere a questa pagina");
        });
    }
}

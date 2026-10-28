package it.pagopa.send.web.mittente.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

@Url("${url.notifiche.mittente.api-keys}")
public interface APIKeyPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"login-page-title\"]")
    Readable<String> supportHeader();

    @XPath("//*[@data-testid=\"generateApiKey\"]")
    Button generateApiKeyButton();

    // pagina "Genera una API Key" aperta con "Genera API Key"
    @XPath("//main")
    NewAPIKeyPage newApiKey();

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
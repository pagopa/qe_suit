package it.pagopa.send.steps.mittenti;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.mittente.api-keys}#selfCareToken=${token.mittente}")
public interface APIKeyPage extends Page {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @XPath("//*[@id=\"login-page-title\"]")
    Readable<String> supportHeader();

    @Override
    default void assertLoaded() {
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h.getText()).isIn("API Key", "API Key");
       });
    }

    default void assertSupportCannotSeeApiKey() {
        supportHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h.getText()).isIn("You do not have the necessary authorizations to access this page");
        });
    }
}
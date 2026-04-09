package it.pagopa.send.steps.mittenti;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.selfcare.notifiche.base}/api-keys/nuova-api-key#selfCareToken=${token.mittente}")
public interface NewAPIKey extends Page {

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> header();

    @Override
    default void assertLoaded() {
       header().readAndAssert((h) -> {
           Assertions.assertThat(h).isNotNull();
           Assertions.assertThat(h.getText()).isIn("Genera una API Key", "Generate an API Key");
       });
    }
}
package it.pagopa.send.steps.mittenti;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.selfcare.notifiche.base}/dashboard/nuova-notifica#selfCareToken=${token.mittente}")
public interface CreateNotification extends Page {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h.getText()).isIn("Nuova notifica", "New notification");
        });
    }
}

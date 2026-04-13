package it.pagopa.send.steps.mittenti;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

@Url("${url.selfcare.notifiche.base}/dashboard#selfCareToken=${token.mittente}")
public interface NotificationDetails extends Page {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@id=\"notificationsTable.body.row\"]/td[7]/button")
    Clickable seeDetailsButton();

    @Override
    default void assertLoaded() {
        seeDetailsButton().click();
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h.getText()).isIn("Dettaglio notifica", "Notification details");
        });
    }


}

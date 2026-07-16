package it.pagopa.send.web.notification_details.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.infrastructure.suit.component.BackNavigable;
import org.assertj.core.api.Assertions;

@Url("da mappare")
public interface NotificationStatusDetailsPage extends Page, BackNavigable {

    @XPath("breadcrumbs")
    Readable<String> breadcrumbs();

    @XPath("header pagina")
    Readable<String> header();

    @Override
    @XPath("torna al dettaglio della notifica")
    Clickable backButton();

    @Override
    default void assertLoaded() {
        header().readAndAssert(h -> {
            Assertions.assertThat(h).isNotBlank();
            Assertions.assertThat(h).isIn("Stato della notifica");
            Assertions.assertThat(breadcrumbs().read()).isNotBlank();
        });
    }
}

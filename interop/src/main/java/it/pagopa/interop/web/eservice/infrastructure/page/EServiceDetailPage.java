package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.time.Duration;

@Url("${interop.web.catalog}/${eserviceId}/${descriptorId}")
public interface EServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//button[normalize-space()='Richiedi fruizione']")
    Button agreementButton();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            String eServiceName = PollingUtils.pollUntil(
                    () -> pageTitle().read(),
                    title -> title != null && !title.isBlank(),
                    Duration.ofSeconds(10),
                    Duration.ofMillis(500),
                    "Page title never became non-blank"
            );
            softly.assertThat(eServiceName).as("Page title is not blank").isNotBlank();
            agreementButton().assertLoaded();
        });
    }
}

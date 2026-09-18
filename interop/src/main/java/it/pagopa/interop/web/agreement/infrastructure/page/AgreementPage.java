package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import it.frontend.e2e.framework.web.capability.core.Readable;

import java.util.List;
import java.util.Optional;

@Url("${interop.web.agreement}/${agreementId}")
public interface AgreementPage extends Page {

    @XPath(".//h2")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    List<Alert> alerts();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Gestisci richiesta");
            softly.assertThat(pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank()));
        });
    }

}
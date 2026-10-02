package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

@Url("${interop.web.catalog}/${eserviceId}/${descriptorId}")
public interface EServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//button[normalize-space()='Richiedi fruizione']")
    Button agreementButton();

    @XPath(".//p[text()='Soglia giornaliera per fruitore']/parent::div/following-sibling::div//span")
    Label consumerDailyThreshold();

    @XPath(".//p[text()='Soglia giornaliera totale']/parent::div/following-sibling::div//span")
    Label totalDailyThreshold();

    @XPath(".//p[text()='Per il tuo ente']/parent::div/following-sibling::div//span")
    Label yourTenantDailyThreshold();

    @XPath(".//h3[text()='Soglie di chiamate API personalizzate']/ancestor::section[1]//p[not(text()='Per il tuo ente')]/parent::div/following-sibling::div//span")
    Label otherTenantDailyThreshold();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            softly.assertThat(pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank()));
            agreementButton().assertLoaded();
        });
    }
}

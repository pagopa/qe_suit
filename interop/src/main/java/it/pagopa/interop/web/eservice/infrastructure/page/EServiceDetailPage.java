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

    @XPath(".//h2[normalize-space()='Soglie e attributi']")
    Label thresholdsAndAttributesTitle();

    @XPath(".//h3[normalize-space()='Soglie di chiamate API']")
    Label apiCallsThresholdTitle();

    @XPath(".//h3[normalize-space()='Soglie di chiamate API personalizzate']")
    Label customApiCallsThresholdTitle();

    @XPath(".//p[normalize-space()='Soglia giornaliera per fruitore']/parent::div/following-sibling::div//span")
    Label consumerDailyThreshold();

    @XPath(".//p[normalize-space()='Soglia giornaliera totale']/parent::div/following-sibling::div//span")
    Label totalDailyThreshold();

    @XPath(".//p[normalize-space()='Per il tuo ente']/parent::div/following-sibling::div//span")
    Label yourTenantDailyThreshold();

    @XPath(".//h3[normalize-space()='Soglie di chiamate API personalizzate']/ancestor::section[1]//p[not(normalize-space()='Per il tuo ente')]/parent::div/following-sibling::div//span")
    Label otherTenantDailyThreshold();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            pageTitle().readAndAssert(title -> Assertions.assertThat(title).as("Page title is not blank").isNotBlank());
            agreementButton().assertLoaded();
        });
    }
}

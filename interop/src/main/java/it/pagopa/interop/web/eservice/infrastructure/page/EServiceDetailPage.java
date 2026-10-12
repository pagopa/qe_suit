package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.eservice.infrastructure.page.detail.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

/**
 * Catalog e-service detail page (route {@code /catalogo-e-service/:eserviceId/:descriptorId}).
 * <p>
 * Selectors derive from the frontend sources (ConsumerEServiceDetails.page.tsx and children).
 * Selectors marked "NOT VERIFIED" depend on the DOM of {@code InformationContainer}
 * (@pagopa/interop-fe-commons), whose source is not available: label and value are assumed
 * to be adjacent siblings.
 * <p>
 * {@link #assertLoaded()} only waits for the page to be rendered; it does not assert any
 * copy, state or presence belonging to the contract matrix.
 */
@Url("${interop.web.catalog}/${eserviceId}/${descriptorId}")
public interface EServiceDetailPage extends
        Page,
        EServiceDetailHeaderLocators,
        EServiceGeneralInfoLocators,
        EServicePurposeTemplatesLocators,
        EServiceSignalHubLocators,
        EServiceAttributesLocators,
        EServiceCatalogListElementsLocators {

    // ------------------------------------------------------------------ Load

    
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

    /**
     * Waits for breadcrumbs, the page title, and the agreement action. Does not assert matrix expectations.
     */
    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            softly.assertThat(pageTitle().read()).as("Page title").isNotBlank();
            softly.assertThat(agreementButton().get()).as("Request agreement button").isPresent();
        });
    }
}

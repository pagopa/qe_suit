package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

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
public interface EServiceDetailPage extends Page {

    EServiceDetailPageHeader header();
    EServiceDetailPagePrimaryActions primaryActions();
    List<Alert> alerts();
    EServiceDetailPageTabs tabs();
    @XPath(".//section[./div[1]//h2[normalize-space()='Informazioni generali']]")
    EServiceDetailPageGeneralInfo generalInfo();
    @XPath(".//section[./div[1]//h2[normalize-space()='Compilazione agevolata della finalità']]")
    EServiceDetailPagePurposeTemplateSection purposeTemplateSection();
    @XPath(".//section[./div[1]//h2[normalize-space()='Signal Hub']]")
    EServiceDetailPageSignalHub signalHub();
    @XPath(".//section[./div[1]//h2[normalize-space()='Soglie e attributi']]")
    EServiceDetailPageThresholdsAndAttributes thresholdsAndAttributes();

    // ------------------------------- Elements of the catalog list (not expected)
    @XPath(".//h1[normalize-space()='Catalogo degli e-service']")
    Label catalogListTitle();
    @XPath(".//label[contains(normalize-space(), 'Cerca per nome')]")
    Label searchByNameField();
    @XPath(".//label[contains(normalize-space(), 'Cerca per erogatore')]")
    Label searchByProducerField();

    // ------------------------------------------------------------------ Load

    /**
     * Waits for the page header (breadcrumbs, non-blank e-service title, and primary agreement action)
     * to be rendered. Does not assert contract matrix expectations.
     */
    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(header().breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            softly.assertThat(header().pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank()));
            softly.assertThat(primaryActions().agreementButton().get(FindPolicy.PRESENT));
        });
    }
}

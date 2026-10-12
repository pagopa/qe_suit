package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.eservice.infrastructure.page.detail.*;
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

    /**
     * Waits for breadcrumbs, the page title, and the agreement action. Does not assert matrix expectations.
     */
    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            softly.assertThat(pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank()));
            agreementButton().assertLoaded();
        });
    }
}

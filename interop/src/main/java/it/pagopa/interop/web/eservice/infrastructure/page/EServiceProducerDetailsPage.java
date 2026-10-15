package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.page.component.details.EServiceDetailsField;

import java.util.List;

/**
 * Direct entry to an authenticated producer e-service version. Selectors describe the
 * details panel and its initial controls; business actions and mobile layout are not modelled.
 */
@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}")
public interface EServiceProducerDetailsPage extends Page {
    @XPath(".//main//h1")
    Label title();

    @XPath(".//main//nav//li[not(@aria-hidden='true')]")
    Label breadcrumbs();

    @XPath(".//main//h2 | .//main//h3")
    Label headings();

    @XPath(".//main//button")
    Label buttons();

    @XPath(".//main//*[@role='tab']")
    List<Button> tabs();

    @XPath(".//main//*[@role='tabpanel']")
    List<Label> panels();

    @XPath(".//main//section//div[div/p][count(div)=2]")
    List<EServiceDetailsField> fields();

    @XPath(".//main//section[not(ancestor::section)]")
    List<Label> sections();

    @XPath(".//main//section/div[1]//p[normalize-space() and not(descendant::p)]")
    Label explanations();

    @XPath(".//main//section[not(ancestor::section)][.//h2[normalize-space()='Specifiche tecniche e-service'] or .//h3[normalize-space()='Specifiche tecniche e-service']]//a")
    Label technicalLinks();

    @XPath(".//main//h2[not(ancestor::section)]/parent::*//div/span")
    Label versionStatus();

    @Override
    default void assertLoaded() {
        title().get(FindPolicy.PRESENT).orElseThrow(
                () -> new IllegalStateException("Producer e-service title is missing"));
        // Readiness only: the complete heading contract is checked by the scenarios.
        if (headings().getAll()
                .orElseThrow(() -> new IllegalStateException("Producer e-service headings are missing"))
                .stream()
                .noneMatch(heading -> "Informazioni generali".equals(heading.getText()))) {
            throw new IllegalStateException("Producer general information has not loaded");
        }
    }
}

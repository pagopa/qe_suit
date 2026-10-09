package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.common.infrastructure.suit.component.CertifiedAttributeGroup;
import it.pagopa.interop.common.infrastructure.suit.component.CertifiedAttributeTab;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}")
public interface EServiceViewPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath("//h3[contains(., 'Attributi Certificati')]/ancestor::section/div[contains(@class, 'MuiBox-root')]/div[contains(@class, 'MuiStack-root')]")
    List<CertifiedAttributeGroup> certifiedAttributeGroups();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza e-service");
            pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank());
        });
    }
}

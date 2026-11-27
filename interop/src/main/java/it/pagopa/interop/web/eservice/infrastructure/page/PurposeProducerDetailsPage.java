package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.interop.web.eservice.infrastructure.page.component.ProducerPurposeDetailsGeneralInfoSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.ProducerPurposeDetailsLoadEstimateSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.ProducerPurposeDetailsTechnicalInfoSection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

import java.util.List;

@Url("${interop.web.base-url}/erogazione/finalita/${purposeId}")
public interface PurposeProducerDetailsPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//a[contains(.,'Torna alle finalità')]")
    Button backToPurposesButton();

    @XPath(".//h1")
    Readable<String> purposeTitle();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//button[normalize-space()='Sospendi']")
    Button suspendButton();

    @XPath(".//*[@role='alert']")
    List<Alert> alerts();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    ProducerPurposeDetailsGeneralInfoSection generalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Specifiche tecniche finalità']]")
    ProducerPurposeDetailsTechnicalInfoSection technicalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Stima di chiamate API']]")
    ProducerPurposeDetailsLoadEstimateSection loadEstimateSection();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "Finalità ricevute", "Visualizza finalità");
        purposeTitle().readAndAssert(title -> Assertions.assertThat(title)
                .as("Purpose title")
                .isNotBlank());
    }
}


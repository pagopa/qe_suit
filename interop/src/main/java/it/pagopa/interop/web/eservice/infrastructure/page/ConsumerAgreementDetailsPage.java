package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.interop.web.eservice.infrastructure.page.component.AgreementDetailsAttributesSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.AgreementDetailsGeneralInfoSection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

import java.util.List;

@Url("${interop.web.base-url}/fruizione/richieste/${agreementId}")
public interface ConsumerAgreementDetailsPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//a[normalize-space()='Torna alle richieste']")
    Button backToRequestsButton();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//*[@role='alert']")
    List<Alert> alerts();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    AgreementDetailsGeneralInfoSection generalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Attributi Certificati']]")
    AgreementDetailsAttributesSection certifiedAttributesSection();

    @XPath(".//section[.//h2[normalize-space()='Attributi Verificati']]")
    AgreementDetailsAttributesSection verifiedAttributesSection();

    @XPath(".//section[.//h2[normalize-space()='Attributi Dichiarati']]")
    AgreementDetailsAttributesSection declaredAttributesSection();

    @XPath(".//button[normalize-space()='Archivia']")
    Button archiveButton();

    @XPath(".//button[normalize-space()='Sospendi']")
    Button suspendButton();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().getPath())
                .as("Breadcrumbs")
                .isEqualTo("Fruizione/Richieste inoltrate/Gestisci richiesta");
        pageTitle().readAndAssert("Gestisci richiesta di fruizione");
    }
}
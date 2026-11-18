package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.interop.web.eservice.infrastructure.page.component.AgreementDetailsAttributesSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.CertifiedAttributesDrawer;
import it.pagopa.interop.web.eservice.infrastructure.page.component.ConsumerAgreementUpdateGeneralInfoSection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;

@Url("${interop.web.base-url}/fruizione/richieste/${agreementId}/modifica")
public interface ConsumerAgreementUpdatePage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath("//h1")
    Readable<String> pageTitle();

    @XPath(".//a[normalize-space()='Torna alle richieste']")
    Button backToRequestsButton();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//*[@role='alert']")
    List<Alert> alerts();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    ConsumerAgreementUpdateGeneralInfoSection generalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Attributi Verificati']]")
    AgreementDetailsAttributesSection verifiedAttributesSection();

    @XPath(".//section[.//h2[normalize-space()='Attributi Dichiarati']]")
    AgreementDetailsAttributesSection declaredAttributesSection();

    CertifiedAttributesDrawer certifiedAttributesDrawer();

    @XPath(".//button[normalize-space()='Elimina bozza']")
    Button deleteDraftButton();

    @XPath(".//button[normalize-space()='Salva bozza']")
    Button saveDraftButton();

    @XPath(".//button[normalize-space()='Inoltra richiesta']")
    Button submitRequestButton();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> "Fruizione/Richieste inoltrate/Modifica richiesta".equals(breadcrumbs().getPath())
                        && "Gestisci richiesta di fruizione".equals(pageTitle().read()),
                isLoaded -> isLoaded,
                "ConsumerAgreementUpdatePage not loaded"
        );
    }
}


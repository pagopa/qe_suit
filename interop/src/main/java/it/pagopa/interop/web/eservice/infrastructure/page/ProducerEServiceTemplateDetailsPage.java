package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Drawer;
import it.pagopa.infrastructure.suit.component.Snackbar;
import it.pagopa.interop.web.eservice_template.infrastructure.page.component.ProducerEServiceTemplateDetailsTab;
import it.pagopa.interop.web.eservice_template.infrastructure.page.component.ProducerEServiceTemplateTenantsTab;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}")
public interface ProducerEServiceTemplateDetailsPage extends Page {

    @XPath(".//a[normalize-space()='Torna ai template'] | .//button[normalize-space()='Torna ai template']")
    Button backToTemplatesButton();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//button[normalize-space()='Crea bozza da template']")
    Button createNewDraftButton();

    @XPath(".//button[normalize-space()='Gestisci bozza']")
    Button manageDraftButton();

    @XPath(".//button[normalize-space()='Pubblica bozza']")
    Button publishDraftButton();

    @XPath(".//button[normalize-space()='Elimina bozza']")
    Button deleteDraftButton();

    @XPath(".//button[normalize-space()='Sospendi']")
    Button suspendButton();

    @XPath(".//button[normalize-space()='Attiva']")
    Button activateButton();

    @XPath(".//button[@role='tab' and contains(.,'Dettagli template')]")
    Button detailsTabButton();

    @XPath(".//button[@role='tab' and contains(.,'Enti aderenti')]")
    Button tenantsTabButton();

    @XPath(".//div[@role='tabpanel' and not(@hidden) and .//h2[normalize-space()='Informazioni template']]")
    ProducerEServiceTemplateDetailsTab detailsTabPanel();

    @XPath(".//div[@role='tabpanel' and not(@hidden) and not(.//h2[normalize-space()='Informazioni template'])]")
    ProducerEServiceTemplateTenantsTab tenantsTabPanel();

    @XPath("(.//button[normalize-space()='Aggiungi attributi'])[1]")
    Button addAttributesButton();

    @XPath("//div[contains(@class, 'MuiDrawer-root')]//button[normalize-space()='Salva modifiche']")
    Button attributesDrawerSaveButton();

    @XPath("//main[not(//div[contains(@class, 'MuiDrawer-root') and not(@aria-hidden='true')])]")
    Drawer closedAttributesDrawer();

    @XPath("//main[not(//div[contains(@class, 'MuiSnackbar-root')]//div[contains(@class, 'MuiAlert-standardError') or contains(@class, 'MuiAlert-outlinedError')])]")
    Snackbar noErrorSnackbar();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                name -> !name.isBlank(),
                "Producer E-Service Template Detail Page is not loaded yet. Page title is blank"
        );
    }
}


package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Drawer;
import it.pagopa.infrastructure.suit.component.Snackbar;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;

import java.util.List;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}")
public interface ProducerEServiceTemplateDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath("(.//button[normalize-space()='Aggiungi attributi'])[1]")
    Button addAttributesButton();

    @XPath("//div[contains(@class, 'MuiDrawer-root')]//button[normalize-space()='Salva modifiche']")
    Button attributesDrawerSaveButton();

    @XPath("//main[not(//div[contains(@class, 'MuiDrawer-root') and not(@aria-hidden='true')])]")
    Drawer closedAttributesDrawer();

    @XPath("//main[not(//div[contains(@class, 'MuiSnackbar-root')]//div[contains(@class, 'MuiAlert-standardError') or contains(@class, 'MuiAlert-outlinedError')])]")
    Snackbar noErrorSnackbar();

    @XPath("(.//p[normalize-space()='Elenco documenti']/parent::div/following-sibling::div//button)[1]")
    Button apiSpecsDownloadButton();

    @XPath(".//p[normalize-space()='Elenco documenti']/parent::div/following-sibling::div//button")
    List<Button> documentationDownloadButtons();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                name -> !name.isBlank(),
                "Producer E-Service Template Detail Page is not loaded yet. Page title is blank"
        );
    }
}


package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-e-service-list}/${eserviceId}/${descriptorId}")
public interface ProducerEServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath("(.//button[normalize-space()='Aggiungi attributi'])[1]")
    Button addAttributesButton();

    @XPath("//div[contains(@class, 'MuiDrawer-root')]//button[normalize-space()='Salva modifiche']")
    Button attributesDrawerSaveButton();

    @XPath("//main[not(//div[contains(@class, 'MuiDrawer-root') and not(@aria-hidden='true')])]")
    Component closedAttributesDrawer();

    @XPath("//main[not(//div[contains(@class, 'MuiSnackbar-root')]//div[contains(@class, 'MuiAlert-standardError') or contains(@class, 'MuiAlert-outlinedError')])]")
    Component noErrorSnackbar();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "I miei e-service", "Visualizza e-service");
        pageTitle().readAndAssert(name -> Assertions.assertThat(name)
                .as("Page title is not blank")
                .isNotBlank());
    }
}

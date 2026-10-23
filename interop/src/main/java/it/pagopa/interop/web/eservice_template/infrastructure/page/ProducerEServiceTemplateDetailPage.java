package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Snackbar;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}")
public interface ProducerEServiceTemplateDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath("(.//button[normalize-space()='Aggiungi attributi'])[1]")
    Button addAttributesButton();

    @XPath("//div[contains(@class, 'MuiDrawer-root')]//button[normalize-space()='Salva modifiche']")
    Button attributesDrawerSaveButton();

    Snackbar snackbar();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert(name -> Assertions.assertThat(name)
                .as("Page title is not blank")
                .isNotBlank());
    }
}


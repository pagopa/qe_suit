package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

@Url("${interop.web.my-eservice-template}/${eserviceTemplateId}/${eserviceTemplateVersionId}")
public interface TemplateEServiceDetailErogatorePage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//button[normalize-space()='Aggiungi attributi']")
    Button addAttributes();

    @XPath(".//button[normalize-space()='Salva modifiche']")
    Button saveAttribute();

    @XPath(".//p[contains(normalize-space(), 'Inserisci le informazioni di contesto e il codice riportato sotto')]")
    Readable<String> errorNotification();

    default String getErrorNotificationAfterAttemptingSaveOfEmptyAttribute() {
        addAttributes().click();
        saveAttribute().click();
        return errorNotification().read();
    }

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Visualizza template");
            softly.assertThat(pageTitle().readAndAssert(eServiceTemplateName -> Assertions.assertThat(eServiceTemplateName).as("Page title is not blank").isNotBlank()));
        });
    }

    default void assertNoErrorNotification(){
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getErrorNotificationAfterAttemptingSaveOfEmptyAttribute()).as("Assert there's no error when trying to save empty attribute").isNullOrEmpty();
        });
    }
}


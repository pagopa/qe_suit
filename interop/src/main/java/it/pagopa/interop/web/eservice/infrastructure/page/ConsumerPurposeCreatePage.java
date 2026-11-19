package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposePreliminaryInformationSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposeTemplateSection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;


@Url("${interop.web.base-url}/fruizione/finalita/${purposeId}/crea")
public interface ConsumerPurposeCreatePage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//a[contains(.,'Torna alle finalità')]")
    Button backToPurposesButton();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//p[normalize-space()='*Campi obbligatori']")
    Label mandatoryFieldsLabel();

    @XPath(".//section[.//h2[normalize-space()='Informazioni preliminari']]")
    PurposePreliminaryInformationSection preliminaryInformationSection();

    @XPath(".//section[.//h2[normalize-space()='Compilazione agevolata della finalità']]")
    PurposeTemplateSection purposeTemplateSection();

    @XPath(".//button[normalize-space()='Crea bozza']")
    Button createDraftButton();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Fruizione", "Finalità inoltrate", "Crea finalità da template");
        pageTitle().readAndAssert("Crea finalità");
    }
}
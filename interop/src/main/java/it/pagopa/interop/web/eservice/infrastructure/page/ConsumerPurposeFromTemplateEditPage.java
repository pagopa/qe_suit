package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposeTemplateGeneralStepPanel;
import it.pagopa.interop.web.eservice.infrastructure.page.component.PurposeTemplateRiskAnalysisStepPanel;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;


@Url("${interop.web.base-url}/fruizione/finalita/${purposeId}/${purposeTemplateId}/modifica")
public interface ConsumerPurposeFromTemplateEditPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//a[normalize-space()='Torna alle finalità']")
    Button backToPurposesButton();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//p[normalize-space()='*Campi obbligatori']")
    Label mandatoryFieldsLabel();

    @XPath(".//*[@data-testid='mobileStepper']")
    Stepper stepper();

    @XPath("//form[@novalidate]")
    PurposeTemplateGeneralStepPanel generalStepPanel();

    @XPath("//form[@novalidate]")
    PurposeTemplateRiskAnalysisStepPanel riskAnalysisStepPanel();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs should be present and in the correct order")
                .containsExactly("Fruizione", "Finalità inoltrate", "Modifica finalità da template");

        pageTitle().readAndAssert("Modifica finalità");
    }
}

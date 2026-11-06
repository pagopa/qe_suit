package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice_template.infrastructure.page.component.TemplateDocumentSummaryCard;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}/modifica/riepilogo")
public interface ProducerEServiceTemplateSummaryPage extends Page {
    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='Specifiche tecniche']]")
    TemplateDocumentSummaryCard technicalSpecsCard();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='Informazioni aggiuntive']]")
    TemplateDocumentSummaryCard additionalInfoCard();

    @Override
    default void assertLoaded() {
        Assertions.assertThat(pageTitle().read()).as("Titolo riepilogo template").isNotBlank();
        technicalSpecsCard().header().title().readAndAssert("Specifiche tecniche");
        additionalInfoCard().header().title().readAndAssert("Informazioni aggiuntive");
    }
}

package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.AdditionalInfoTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.GeneralInfoTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.TechnicalSpecsTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.ThresholdsAttributesTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;

@Url("${interop.web.base-url}/erogazione/template-eservice/${eserviceId}/${descriptorId}/modifica/riepilogo")
public interface ProducerEServiceTemplateSummaryPage extends Page {

    @XPath("//a[contains(., 'Torna ai template')]")
    Button backToTemplatesButton();

    Breadcrumbs breadcrumbs();

    @XPath("//h1")
    Label pageTitle();

    @XPath("//div[contains(@class,'MuiChip-root')]//span[contains(.,'in bozza')]")
    Label statusChip();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]")
    List<EServiceSummaryCard> summaryCards();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='Informazioni generali']]")
    GeneralInfoTemplateEServiceCard generalInfoTemplateEServiceCard();

    //The same card as in ProducerEServiceSummaryPage is used here, because they are the same.
    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='Soglie e attributi']]")
    ThresholdsAttributesTemplateEServiceCard thresholdsAttributesTemplateEServiceCard();

    //The same card as in ProducerEServiceSummaryPage is used here, because they are the same.
    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='Specifiche tecniche']]")
    TechnicalSpecsTemplateEServiceCard technicalSpecsTemplateEServiceCard();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='Informazioni aggiuntive']]")
    AdditionalInfoTemplateEServiceCard additionalInfoTemplateEServiceCard();

    @XPath("//button[contains(., 'Elimina bozza')]")
    Button deleteDraftButton();

    @XPath("//button[contains(., 'Modifica bozza')]")
    Button editDraftButton();

    @XPath("//button[contains(., 'Pubblica')]")
    Button publishButton();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> summaryCards().size(),
                size -> size > 0,
                "Summary cards should be present"
        );
    }
}

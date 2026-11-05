package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.GeneralInfoCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.ThresholdsAttributesCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.TechnicalSpecsCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.AdditionalInfoCard;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;

@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}/modifica/riepilogo")
public interface ProducerEServiceSummaryPage extends Page {

    String BTN_BACK_ESERVICE = "Torna agli e-service";
    String STATUS_DRAFT = "in bozza";
    String CARD_GENERAL_INFO = "Informazioni generali";
    String CARD_THRESHOLDS_ATTRIBUTES = "Soglie e attributi";
    String CARD_TECH_SPECS = "Specifiche tecniche";
    String CARD_ADDITIONAL_INFO = "Informazioni aggiuntive";
    String BTN_DELETE_DRAFT = "Elimina bozza";
    String BTN_EDIT_DRAFT = "Modifica bozza";
    String BTN_PUBLISH = "Pubblica";

    @XPath("//a[contains(., '" + BTN_BACK_ESERVICE + "')]")
    Button backToEServicesButton();

    Breadcrumbs breadcrumbs();

    @XPath("//h1")
    Label pageTitle();

    @XPath("//div[contains(@class,'MuiChip-root')]//span[contains(translate(., '" + STATUS_DRAFT + "', 'in bozza'),'in bozza')]")
    Label statusChip();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]")
    List<EServiceSummaryCard> summaryCards();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='" + CARD_GENERAL_INFO + "']]")
    GeneralInfoCard generalInfoCard();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='" + CARD_THRESHOLDS_ATTRIBUTES + "']]")
    ThresholdsAttributesCard thresholdsAttributesCard();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='" + CARD_TECH_SPECS + "']]")
    TechnicalSpecsCard technicalSpecsCard();

    @XPath(".//*[contains(@class,'MuiAccordion-root')]"
            + "[.//*[contains(@class,'MuiAccordionSummary-root')]"
            + "//h6[normalize-space(.)='" + CARD_ADDITIONAL_INFO + "']]")
    AdditionalInfoCard additionalInfoCard();

    @XPath("//button[contains(., '" + BTN_DELETE_DRAFT + "')]")
    Button deleteDraftButton();

    @XPath("//button[contains(., '" + BTN_EDIT_DRAFT + "')]")
    Button editDraftButton();

    @XPath("//button[contains(., '" + BTN_PUBLISH + "')]")
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

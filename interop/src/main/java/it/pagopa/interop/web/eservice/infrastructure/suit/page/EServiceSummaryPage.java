package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.EServiceSummaryCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.GeneralInfoCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.ThresholdsAttributesCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.TechnicalSpecsCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.AdditionalInfoCard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}/modifica/riepilogo")
public interface EServiceSummaryPage extends Page {

    String BTN_BACK_ESERVICE = "Torna agli e-service";
    String BREADCRUMB_EROGAZIONE = "Erogazione";
    String BREADCRUMB_MY_ESERVICES = "I miei e-service";
    String BREADCRUMB_EDIT_ESERVICE = "Modifica e-service";
    String BREADCRUMB_SUMMARY = "Riepilogo";
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

    @XPath("//nav[contains(@class,'MuiBreadcrumbs-root')]//a[contains(., '" + BREADCRUMB_EROGAZIONE + "')]")
    Link erogazioneBreadcrumb();

    @XPath("//nav[contains(@class,'MuiBreadcrumbs-root')]//a[contains(., '" + BREADCRUMB_MY_ESERVICES + "')]")
    Link myEServicesBreadcrumb();

    @XPath("//nav[contains(@class,'MuiBreadcrumbs-root')]//a[contains(., '" + BREADCRUMB_EDIT_ESERVICE + "')]")
    Link editEServiceBreadcrumb();

    @XPath("//nav[contains(@class,'MuiBreadcrumbs-root')]//*[self::p or self::span or self::li][contains(., '" + BREADCRUMB_SUMMARY + "')]")
    Label currentBreadcrumb();

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
        assertThat(pageTitle().read()).as("Titolo e-service valorizzato").isNotBlank();
        assertThat(statusChip().get().isPresent()).as("Stato bozza presente").isTrue();
    }
}

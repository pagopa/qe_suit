package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

import static org.assertj.core.api.Assertions.assertThat;

@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}/modifica/riepilogo")
public interface EServiceSummaryPage extends Page {

    String BTN_BACK_ESERVICE = "Torna agli e-service";
    String BREADCRUMB_EROGAZIONE = "Erogazione";
    String BREADCRUMB_MY_ESERVICES = "I miei e-service";
    String BREADCRUMB_EDIT_ESERVICE = "Modifica e-service";
    String BREADCRUMB_SUMMARY = "Riepilogo";
    String STATUS_DRAFT = "In bozza";
    String CARD_GENERAL_INFO = "Informazioni generali";
    String CARD_THRESHOLDS_ATTRIBUTES = "Soglie e attributi";
    String CARD_TECH_SPECS = "Specifiche tecniche";
    String CARD_ADDITIONAL_INFO = "Informazioni aggiuntive";
    String BTN_DELETE_DRAFT = "Elimina bozza";
    String BTN_EDIT_DRAFT = "Modifica bozza";
    String BTN_PUBLISH = "Pubblica";

    @XPath("//button[contains(., '" + BTN_BACK_ESERVICE + "')]")
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

    @XPath("//div[contains(@class,'MuiAccordionSummary-root') or contains(@class,'MuiPaper-root')]//h6[contains(., '" + CARD_GENERAL_INFO + "')]")
    Label generalInfoCardHeader();

    @XPath("//div[contains(@class,'MuiAccordionSummary-root') or contains(@class,'MuiPaper-root')]//h6[contains(., '" + CARD_THRESHOLDS_ATTRIBUTES + "')]")
    Label thresholdsAttributesCardHeader();

    @XPath("//div[contains(@class,'MuiAccordionSummary-root') or contains(@class,'MuiPaper-root')]//h6[contains(., '" + CARD_TECH_SPECS + "')]")
    Label techSpecsCardHeader();

    @XPath("//div[contains(@class,'MuiAccordionSummary-root') or contains(@class,'MuiPaper-root')]//h6[contains(., '" + CARD_ADDITIONAL_INFO + "')]")
    Label additionalInfoCardHeader();

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

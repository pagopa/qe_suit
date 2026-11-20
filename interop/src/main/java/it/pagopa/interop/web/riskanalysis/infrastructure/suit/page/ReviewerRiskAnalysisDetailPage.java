package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.APIEstimateCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.AssignmentCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.PurposeRiskAnalysisGeneralInfoCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RejectionReasonDrawer;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}/dettaglio")
public interface ReviewerRiskAnalysisDetailPage extends Page {

    @XPath(".//a[contains(@href, '/ui/it/analisi-del-rischio') and contains(text(), 'Torna alle analisi del rischio')]")
    Button backButton();

    @XPath(".//nav[contains(@class, 'MuiBreadcrumbs-root')]")
    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//div[contains(@class, 'MuiChip-root')]")
    Label statusChip();

    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert rejectionAlert();

    @XPath(".//button[text()='Leggi motivazione']")
    Button readRejectionReasonButton();

    @XPath(".//div[contains(@class, 'MuiTabs-root')]")
    Tabs tabs();

    @XPath(".//button[@role='tab' and normalize-space()='Dettagli finalità']")
    Button purposeDetailsTab();

    @XPath(".//button[@role='tab' and normalize-space()='Analisi del rischio']")
    Button riskAnalysisTab();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    PurposeRiskAnalysisGeneralInfoCard generalInfoCard();

    @XPath(".//section[.//h2[normalize-space()='Stima chiamate API']]")
    APIEstimateCard apiEstimateCard();

    @XPath(".//section[.//h2[normalize-space()='Assegnazione']]")
    AssignmentExtendedCard assignmentCard();

    @XPath(".//section[.//h2[normalize-space()='Analisi del rischio']]")
    RiskAnalysisInfoCard riskAnalysisInfoCard();

    @XPath("(.//section[.//h2[normalize-space()='Analisi del rischio']])[1]/following-sibling::section[1]")
    RiskAnalysisCard riskAnalysisCard();

    @XPath("//div[contains(@class, 'MuiDrawer-paper')]")
    RejectionReasonDrawer rejectionReasonDrawer();

    interface AssignmentExtendedCard extends AssignmentCard {
        @XPath(".//h2")
        Label title();

        @XPath(".//p[normalize-space()='Data assegnazione']")
        Label assignmentDateLabel();

        @XPath(".//p[normalize-space()='Data assegnazione']/parent::div/following-sibling::div[1]//span")
        Label assignmentDateValue();

        @XPath(".//p[normalize-space()='Data approvazione']")
        Label approvedDateLabel();

        @XPath(".//p[normalize-space()='Data approvazione']/parent::div/following-sibling::div[1]//span")
        Label approvedDateValue();

        @XPath(".//p[normalize-space()='Approvata da']")
        Label approvedByLabel();

        @XPath(".//p[normalize-space()='Approvata da']/parent::div/following-sibling::div[1]//span")
        Label approvedByValue();
    }

    interface RiskAnalysisInfoCard extends Component {
        @XPath(".//h2")
        Label sectionTitle();

        @XPath(".//p[contains(@class, 'MuiTypography-root') and contains(normalize-space(), 'Non puoi più modificare')]")
        Label subtitle();

        @XPath(".//p[contains(@class, 'MuiTypography-root') and normalize-space()='Trattamento dati personali']")
        Label personalDataTreatment();

        @XPath(".//p[normalize-space()='Trattamento dati personali']/following::span[contains(@class, 'MuiBox-root')][1]")
        Label personalDataTreatmentValue();
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> {
                    String title = pageTitle().read();
                    return title != null && !title.isBlank();
                },
                Boolean.TRUE::equals,
                "Risk Analysis information page is not loaded or  its title is absent" );
    }
}

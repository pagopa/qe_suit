package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisCompleteCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisGeneralInfoCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RejectionReasonDrawer;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/fruizione/finalita/${purposeId}/riepilogo")
public interface ConsumerPurposeSummaryPage extends Page {

    @XPath(".//a[contains(@href,'/fruizione/finalita')]")
    Button backToPurposesButton();

    @XPath(".//nav[contains(@class,'MuiBreadcrumbs-root')]")
    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//div[contains(@class,'MuiAccordion-root')][1]")
    RiskAnalysisGeneralInfoCard generalInfoCard();

    @XPath(".//div[contains(@class,'MuiAccordion-root')][.//div[contains(@class,'MuiAccordionSummary-root')]//h6[normalize-space(.)='Assegnazione']]")
    PurposeAssignmentCard assignmentCard();

    @XPath(".//div[contains(@class,'MuiAccordion-root')][.//div[contains(@class,'MuiAccordionSummary-root')]//h6[normalize-space(.)='Analisi del rischio']]")
    RiskAnalysisCompleteCard riskAnalysisCard();

    @XPath(".//button[contains(@class,'MuiButton-textError')]")
    Button deleteDraftButton();

    @XPath(".//button[contains(@class,'MuiButton-textPrimary') and normalize-space(.)='Modifica bozza']")
    Button editDraftButton();

    @XPath(".//button[contains(@class,'MuiButton-containedPrimary') and normalize-space(.)='Pubblica']")
    Button publishButton();

    @XPath(".//div[contains(@class,'MuiAlert-root')]")
    Alert pageAlert();

    @XPath(".//div[@role='alert' and contains(@class,'MuiAlert-standardError')]")
    Alert rejectionAlert();

    @XPath(".//div[@role='alert' and contains(@class,'MuiAlert-standardError')]//button[normalize-space(.)='Leggi motivazione']")
    Button readRejectionReasonButton();

    @XPath("//div[contains(@class, 'MuiDrawer-paper')]")
    RejectionReasonDrawer rejectionReasonDrawer();

    @XPath("//div[@role='dialog']")
    Component confirmationDialog();

    @XPath(".//h1")
    Label unauthorizedTitle();

    @XPath(".//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath(".//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();


    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                text -> text != null && !text.isBlank(),
                "Purpose Risk Analysis Summary page is not loaded or its title is absent"
        );
    }

    interface PurposeAssignmentCard extends Component {
        @XPath(".//div[contains(@class, 'MuiAccordionSummary-root') and @role='button']")
        Button header();

        @XPath(".//h6[contains(@class,'MuiTypography-subtitle2')]")
        Label stepNumber();

        @XPath(".//h6[contains(@class,'MuiTypography-h6')]")
        Label title();

        @XPath(".//div[contains(@class,'MuiAccordionDetails-root')]//div[contains(@class,'MuiStack-root')]/div[1]/p")
        Label modeLabel();

        @XPath(".//div[contains(@class,'MuiAccordionDetails-root')]//div[contains(@class,'MuiStack-root')]/div[2]/p")
        Label modeValue();

        @XPath(".//p[normalize-space(.)='Valutatore' or normalize-space(.)='Valutatori']")
        Label assignedToLabel();

        @XPath(".//p[normalize-space(.)='Valutatore' or normalize-space(.)='Valutatori']/parent::div/following-sibling::div[1]/p")
        Label assignedToValue();
    }
}
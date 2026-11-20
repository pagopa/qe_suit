package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Dialog;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisCompleteCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisGeneralInfoCard;
import it.pagopa.utils.async.PollingUtils;

/**
 * Page object for reviewing a submitted risk analysis before approval or rejection.
 */
@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}/approvazione")
public interface ReviewerRiskAnalysisApprovalPage extends Page {

    @XPath(".//a[contains(., 'Torna alle analisi del rischio')]")
    Button backToEServicesButton();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label title();

    @XPath(".//div[contains(@class, 'MuiAlert-message')]")
    Label alertMessage();

    @XPath(".//button[contains(., 'Rifiuta ')]")
    Button rejectButton();

    @XPath(".//button[contains(., 'Approva ')]")
    Button approveButton();

    @XPath(".//div[@role='dialog']")
    Dialog approvalConfirmationDialog();

    @XPath("//div[@role='alert' and (contains(concat(' ', normalize-space(@class), ' '), ' MuiAlert-outlinedError ') or contains(concat(' ', normalize-space(@class), ' '), ' MuiAlert-standardError '))]")
    Alert approvalErrorAlert();

    @XPath(".//h1")
    Label unauthorizedTitle();

    @XPath(".//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath(".//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

    @XPath(".//div[contains(@class, 'MuiAccordion-root')][.//h6[text()='Informazioni generali']]")
    RiskAnalysisGeneralInfoCard generalInfoCard();

    @XPath(".//div[contains(@class, 'MuiAccordion-root')][.//h6[text()='Analisi del rischio']]")
    RiskAnalysisCompleteCard riskAnalysisCard();

    /**
     * Waits for a non-blank page heading; does not verify the accordion contents.
     */
    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Risk Analysis Approval page is not loaded or its title is absent"
        );
    }
}
package it.pagopa.interop.web.riskanalysis.infrastructure.suit.page;

import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.frontend.e2e.framework.web.domain.Page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.GeneralInfoCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisCard;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}/compilazione/riepilogo")
public interface RiskAnalysisSummaryPage extends Page {
    @XPath(".//nav[contains(@class, 'MuiBreadcrumbs-root')]")
    Breadcrumbs breadcrumbs();

    @XPath(".//a[contains(@href, 'analisi-del-rischio') and contains(normalize-space(), 'Torna alle analisi del rischio')]")
    Button backButton();

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//div[@role='alert']")
    Alert infoAlert();

    @XPath(".//div[@role='dialog']")
    Dialog approvalConfirmationDialog();

    @XPath("//div[@role='alert' and (contains(concat(' ', normalize-space(@class), ' '), ' MuiAlert-outlinedError ') or contains(concat(' ', normalize-space(@class), ' '), ' MuiAlert-standardError '))]")
    Alert approvalErrorAlert();

    @XPath(".//button[contains(., 'Modifica bozza')]")
    Button editDraftButton();

    @XPath(".//button[contains(., 'Approva')]")
    Button approveButton();

    @XPath(".//div[contains(@class, 'MuiAccordion-root') and .//h6[normalize-space()='Informazioni generali']]")
    GeneralInfoCard generalInfoCard();

    @XPath(".//div[contains(@class, 'MuiAccordion-root') and .//h6[normalize-space()='Analisi del rischio']]")
    RiskAnalysisCard riskAnalysisCard();


    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> "Riepilogo".equals(pageTitle().read()),
                Boolean.TRUE::equals,
                "Risk Analysis Summary page has not loaded"
        );
    }
}
package it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.APIEstimateCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.AssignmentCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.PurposeRiskAnalysisGeneralInfoCard;
import it.pagopa.utils.async.PollingUtils;

/**
 * Page object for the RA information shown to a reviewer before starting
 * risk-analysis compilation.
 */
@Url("${interop.web.base-url}/analisi-del-rischio/${riskAnalysisId}")
public interface RiskAnalysisInfoPage extends Page {

    @XPath(".//a[contains(normalize-space(), 'Torna alle analisi del rischio')]")
    Button backToEServicesButton();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label title();

    @XPath(".//section[contains(@class, 'MuiPaper-root')][.//h2[normalize-space(.)='Informazioni generali']]")
    PurposeRiskAnalysisGeneralInfoCard generalInfoCard();

    @XPath(".//section[contains(@class, 'MuiPaper-root')][.//h2[normalize-space(.)='Stima chiamate API']]")
    APIEstimateCard apiEstimateCard();

    @XPath(".//section[contains(@class, 'MuiPaper-root')][.//h2[normalize-space(.)='Assegnazione']]")
    AssignmentCard assignmentCard();

    @XPath(".//button[contains(normalize-space(), 'Inizia la compilazione')]")
    Button startCompilationBtn();

    @XPath(".//h1")
    Label unauthorizedTitle();

    @XPath(".//h1/following-sibling::*[1]")
    Label unauthorizedSubtitle();

    @XPath(".//a[@href='/ui/it/' and normalize-space()='Torna alla home']")
    Button backToHomeBtn();

    /**
     * Waits for the RA-information heading; does not verify the section contents.
     */
    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> {
                    String title = title().read();
                    return title != null && !title.isBlank();
                },
                Boolean.TRUE::equals,
                "Risk Analysis information page is not loaded or  its title is absent" );
    }
}

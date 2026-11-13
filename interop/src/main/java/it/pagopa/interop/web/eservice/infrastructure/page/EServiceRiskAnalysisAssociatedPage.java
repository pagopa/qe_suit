package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.page.component.RiskAnalysisOption;
import it.pagopa.interop.web.eservice.infrastructure.page.component.RiskAnalysisSelectionSection;
import it.pagopa.interop.web.eservice.infrastructure.page.component.RiskAnalysisSummarySection;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;

@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}/finalita")
public interface EServiceRiskAnalysisAssociatedPage extends Page {

    // --- Navigazione superiore (PageContainer: backToAction + breadcrumbs) ---
    @XPath(".//a[contains(@class, 'MuiButton-root') and .//*[@data-testid='ArrowBackIcon']]")
    Button backToEServiceButton();

    Breadcrumbs breadcrumbs();

    // --- Sezione 1: selezione dell'analisi del rischio (SectionContainer + RHFAutocompleteSingle) ---
    @XPath("(.//main//section[contains(@class, 'MuiPaper-root') and not(.//ancestor::section)])[1]")
    RiskAnalysisSelectionSection selectionSection();

    // --- Sezione 2: riepilogo (SectionContainer + EServiceRiskAnalysisInfoSummary) ---
    @XPath("(.//main//section[contains(@class, 'MuiPaper-root') and not(.//ancestor::section)])[2]")
    RiskAnalysisSummarySection summarySection();

    // --- Opzioni del menu a tendina (popper renderizzato fuori dal main) ---
    @XPath("//ul[@role='listbox']/li[@role='option']")
    List<RiskAnalysisOption> riskAnalysisOptions();

    @Override
    default void assertLoaded() {
//        PollingUtils.pollUntil(
//                () -> selectionSection().title().get(Gettable.FindPolicy.VISIBLE).isPresent()
//                        && summarySection().title().get(Gettable.FindPolicy.VISIBLE).isPresent()
//                        && !summarySection().questionAnswerItems().isEmpty(),
//                loaded -> loaded,
//                "La pagina EServiceRiskAnalysisAssociatedPage e il riepilogo dell'analisi del rischio devono essere caricati"
//        );
    }
}

package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

import java.util.List;

/**
 * Seconda sezione (SectionContainer) della pagina: riepilogo dell'analisi del rischio
 * (EServiceRiskAnalysisInfoSummary) con sottosezione titolata e lista domande/risposte.
 */
public interface RiskAnalysisSummarySection extends Component {

    @XPath(".//h3")
    Label title();

    @XPath(".//ul/li")
    List<RiskAnalysisQuestionAnswerItem> questionAnswerItems();
}


package it.pagopa.interop.domain.services.risk_analysis;

import it.pagopa.interop.domain.model.RiskAnalysis;

public interface RiskAnalysisService {
    RiskAnalysis createRiskAnalysis();
    RiskAnalysis createRiskAnalysis(boolean completed);
}

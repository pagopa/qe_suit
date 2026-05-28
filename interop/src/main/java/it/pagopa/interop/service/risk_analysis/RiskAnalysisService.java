package it.pagopa.interop.service.risk_analysis;

import it.pagopa.interop.domain.model.RiskAnalysis;

public interface RiskAnalysisService {
    RiskAnalysis createRiskAnalysis();
    RiskAnalysis createRiskAnalysis(boolean completed);
}

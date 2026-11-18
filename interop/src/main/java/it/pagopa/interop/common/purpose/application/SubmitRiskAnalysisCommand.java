package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;

public interface SubmitRiskAnalysisCommand {
    SubmitRiskAnalysisCommand riskAnalysisForm(RiskAnalysisForm riskAnalysisForm);

    RiskAnalysisForm getRiskAnalysisForm();
}

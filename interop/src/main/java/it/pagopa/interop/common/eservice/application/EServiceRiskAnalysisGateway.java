package it.pagopa.interop.new_arch.common.eservice.application;

import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRef;
import it.pagopa.interop.new_arch.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.new_arch.common.risk_analysis.domain.RiskAnalysisForm;
import org.springframework.plugin.core.Plugin;

public interface EServiceRiskAnalysisGateway extends Plugin<Channel> {
    EServiceRiskAnalysis getRiskAnalysis(EServiceRef eServiceRef);

    EServiceRiskAnalysis addRiskAnalysis(EServiceRef eServiceRef, RiskAnalysisForm riskAnalysisForm);
}

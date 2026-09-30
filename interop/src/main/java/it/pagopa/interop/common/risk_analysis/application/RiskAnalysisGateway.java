package it.pagopa.interop.common.risk_analysis.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.springframework.plugin.core.Plugin;

public interface RiskAnalysisGateway extends Plugin<Channel> {
    RiskAnalysisFormConfig getLatestRiskAnalysisConfig(Tenant tenant);
}

package it.pagopa.interop.new_arch.common.risk_analysis.application;

import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.springframework.plugin.core.Plugin;

public interface RiskAnalysisGateway extends Plugin<Channel> {
    RiskAnalysisFormConfig getLatestRiskAnalysisConfig(Tenant tenant);
}

package it.pagopa.interop.bff.risk_analysis.infrastruture;

import it.pagopa.interop.bff.purpose.infratructure.BffPurposeRestClient;
import it.pagopa.interop.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffRiskAnalysisGateway implements RiskAnalysisGateway {

    private final BffPurposeRestClient restClient;
    private final BffRiskAnalysisMapper mapper;

    @Override
    public RiskAnalysisFormConfig getLatestRiskAnalysisConfig(Tenant tenant) {
        return restClient.retrieveLatestRiskAnalysisConfiguration(TenantKind.fromValue(tenant.getTenantType().name()))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toRiskAnalysisFormConfig)
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}

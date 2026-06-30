package it.pagopa.interop.bff.client;

import it.pagopa.interop.bff.client.mapper.PurposeMapper;
import it.pagopa.interop.common.contract.model.shared.enums.TenantKind;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.AbstractRestClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurposeBffClient extends AbstractRestClient {

    private final PurposesApi purposesApi;
    private final PurposeMapper mapper;

    public TestChain<RiskAnalysisFormConfig, it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisFormConfig> retrieveLatestRiskAnalysisConfiguration(TenantKind tenantKind) {
        var bffTenantKind = it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind.fromValue(tenantKind.name());

        return super.read(
                () -> purposesApi.retrieveLatestRiskAnalysisConfigurationWithHttpInfo(bffTenantKind),
                mapper::toRiskAnalysis
        );
    }
}

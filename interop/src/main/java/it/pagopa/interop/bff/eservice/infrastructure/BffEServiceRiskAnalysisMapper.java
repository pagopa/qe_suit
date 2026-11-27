package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffEServiceRiskAnalysisMapper {

    EServiceRiskAnalysis toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis source);

    RiskAnalysisFormSeed toChannel(it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm source);

    @Mapping(target = ".", source = "source")
    @Mapping(target = "tenantKind", source = "tenantKind")
    EServiceTemplateRiskAnalysisSeed toChannel(EServiceRiskAnalysis source, TenantKind tenantKind);
}


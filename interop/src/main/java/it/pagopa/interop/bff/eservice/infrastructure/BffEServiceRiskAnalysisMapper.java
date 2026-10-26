package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import org.mapstruct.Mapper;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffEServiceRiskAnalysisMapper {

    EServiceRiskAnalysis toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis source);
}


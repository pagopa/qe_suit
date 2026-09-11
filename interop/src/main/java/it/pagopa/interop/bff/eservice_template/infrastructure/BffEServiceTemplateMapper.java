package it.pagopa.interop.bff.eservice_template.infrastructure;

import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffEServiceTemplateMapper {

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "versions", ignore = true)
    @Mapping(target = "riskAnalysis", ignore = true)
    @Mapping(target = "riskAnalyses", ignore = true)
    EServiceTemplate toEServiceTemplateBase(EServiceTemplateDetails source);
}


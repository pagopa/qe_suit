package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class}
)
public interface BffAttributeMapper {

    @Mapping(target = "group", ignore = true)
    Attribute toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.Attribute source);
}


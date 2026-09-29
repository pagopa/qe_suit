package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffAttributeMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "kind", source = "kind")
    @Mapping(target = "group", constant = "0")
    Attribute toAttribute(
            it.pagopa.interop.generated.openapi.clients.bff.model.Attribute attribute
    );
}

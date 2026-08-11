package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.mapping.SharedMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffClientMapper {

    @Mapping(target = "consumerId", source = "consumer.id")
    @Mapping(target = "keys", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "admin", source = "admin")
    Client toClient(it.pagopa.interop.generated.openapi.clients.bff.model.Client source);
}
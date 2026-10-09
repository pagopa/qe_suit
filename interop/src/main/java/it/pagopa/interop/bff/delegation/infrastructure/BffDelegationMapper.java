package it.pagopa.interop.bff.delegation.infrastructure;

import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.Delegation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffDelegationMapper {

    @Mapping(target = "id", source = "id")
    it.pagopa.interop.common.delegation.domain.Delegation toDelegation(Delegation consumerDelegation);
}

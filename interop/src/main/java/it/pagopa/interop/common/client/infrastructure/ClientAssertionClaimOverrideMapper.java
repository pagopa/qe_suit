package it.pagopa.interop.common.client.infrastructure;

import it.pagopa.interop.common.client.domain.ClientAssertionClaimOverride;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.utils.jwt.JwtBuilder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapper.class })
public interface ClientAssertionClaimOverrideMapper {

    JwtBuilder.JwtClaimOverride map(ClientAssertionClaimOverride source);

    default List<JwtBuilder.JwtClaimOverride> map(List<ClientAssertionClaimOverride> source) {
        return source == null ? null : source.stream().map(this::map).toList();
    }
}

package it.pagopa.interop.common.client.infrastructure;

import it.pagopa.interop.common.client.domain.ClientAssertionClaimOverride;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.utils.SharedMapperUtils;
import it.pagopa.interop.common.infrastructure.utils.jwt.JwtBuilder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapperUtils.class })
public interface ClientAssertionClaimOverrideMapper {

    JwtBuilder.JwtClaimOverride map(ClientAssertionClaimOverride source);

    default List<JwtBuilder.JwtClaimOverride> map(List<ClientAssertionClaimOverride> source) {
        return source == null ? null : source.stream().map(this::map).toList();
    }
}

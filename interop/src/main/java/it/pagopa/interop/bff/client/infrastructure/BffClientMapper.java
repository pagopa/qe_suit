package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.infrastructure.SharedMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.LinkedHashSet;

@Mapper(
        config = TestMapperConfig.class,
        uses = {SharedMapper.class, BffCommonMapper.class}
)
public interface BffClientMapper {

    default Client toClientPreservingKeysAndUsers(
            it.pagopa.interop.generated.openapi.clients.bff.model.Client source,
            Client existingClient
    ) {
        if (source == null) {
            return existingClient;
        }

        Client mapped = toClient(source);

        if (existingClient == null) {
            return mapped;
        }

        return mapped.toBuilder()
                .keys(existingClient.getKeys() != null
                        ? new LinkedHashSet<>(existingClient.getKeys())
                        : new LinkedHashSet<>())
                .users(existingClient.getUsers() != null
                        ? new ArrayList<>(existingClient.getUsers())
                        : new ArrayList<>())
                .build();
    }

    @Mapping(target = "consumerId", source = "consumer.id")
    @Mapping(target = "keys", ignore = true)
    @Mapping(target = "users", ignore = true)
    Client toClient(
            it.pagopa.interop.generated.openapi.clients.bff.model.Client source
    );
}
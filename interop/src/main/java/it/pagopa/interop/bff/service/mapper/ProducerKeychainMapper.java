package it.pagopa.interop.bff.service.mapper;

import it.pagopa.interop.bff.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychains;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapperUtils.class })
public interface ProducerKeychainMapper {

    @Mapping(target = "keys", ignore = true)
    @Mapping(target = "users", ignore = true)
    ProducerKeychain toProducerKeychain(
            it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain source
    );

    @Mapping(target = "description", ignore = true)
    @Mapping(target = "keys", ignore = true)
    @Mapping(target = "users", ignore = true)
    ProducerKeychain toProducerKeychain(CompactProducerKeychain source);

    List<ProducerKeychain> toProducerKeychains(List<CompactProducerKeychain> source);

    default List<ProducerKeychain> toProducerKeychains(CompactProducerKeychains source) {
        if (source == null || source.getResults() == null) {
            return List.of();
        }

        return toProducerKeychains(source.getResults());
    }

    default ProducerKeychain toDomainWithUpsert(
            it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain source,
            ProducerKeychain current
    ) {
        ProducerKeychain mapped = toProducerKeychain(source);

        if (current == null) {
            return mapped;
        }

        return mapped.toBuilder()
                .keys(current.getKeys())
                .users(current.getUsers())
                .build();
    }

    default ProducerKeychain toDomainCompactWithUpsert(
            CompactProducerKeychain source,
            ProducerKeychain current
    ) {
        ProducerKeychain mapped = toProducerKeychain(source);

        if (current == null) {
            return mapped;
        }

        return mapped.toBuilder()
                .description(current.getDescription())
                .keys(current.getKeys())
                .users(current.getUsers())
                .build();
    }
}
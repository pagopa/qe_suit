package it.pagopa.interop.bff.producer_keychain.infrastructure;

import it.pagopa.interop.common.infrastructure.SharedMapper;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainPublicKey;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeysPage;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(config = TestMapperConfig.class, uses = { SharedMapper.class })
public interface BffProducerKeychainMapper {

    default ProducerKeychain toDomain(
            it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain source,
            ProducerKeychain previous
    ) {
        if (source == null) {
            return previous;
        }

        ProducerKeychain mapped = toProducerKeychain(source);

        if (previous == null) {
            return mapped;
        }

        return mapped.toBuilder()
                .keys(previous.getKeys() != null ? new ArrayList<>(previous.getKeys()) : null)
                .users(previous.getUsers() != null ? previous.getUsers() : null)
                .publicKeys(previous.getPublicKeys() != null ? new ArrayList<>(previous.getPublicKeys()) : null)
                .build();
    }

    @Mapping(target = "keys", ignore = true)
    @Mapping(target = "publicKeys", ignore = true)
    @Mapping(target = "users", ignore = true)
    ProducerKeychain toProducerKeychain(
            it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain source
    );

    @Mapping(target = "id", source = "keyId")
    @Mapping(target = "userId", source = "user")
    @Mapping(target = "orphan", source = "isOrphan")
    ProducerKeychainPublicKey toPublicKey(PublicKey source);

    default ProducerKeysPage toPage(PublicKeys source, int offset, int limit) {
        List<ProducerKeychainPublicKey> keys = source.getKeys() != null
                ? source.getKeys().stream().map(this::toPublicKey).toList()
                : List.of();
        Integer totalCount = source.getPagination() != null ? source.getPagination().getTotalCount() : null;
        return new ProducerKeysPage(keys, offset, limit, totalCount);
    }

    default UUID map(String keyId) {
        return keyId == null ? null : UUID.fromString(keyId);
    }

    default UUID map(CompactUser user) {
        return user == null ? null : user.getUserId();
    }
}

package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.bff.infrastructure.config.StrictMapperConfig;
import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(config = StrictMapperConfig.class)
public interface ProducerKeychainMapper {
    ProducerKeychain toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain dto);
    ProducerKeychain toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey dto);
    ProducerKeychain toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.EncodedClientKey dto);

    default ProducerKeychain toDomain(it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource dto) {
        return null;
    }
    default ProducerKeychain toDomain(Void dto) {
        return null;
    }

    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<ProducerKeychain> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychains dto) {
        return List.of();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<ProducerKeychain> toDomainList(List<it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser> dto) {
        return List.of();
    }
    /*
     * TODO: implementare il mapping del wrapper/list response.
     * Esempio tipico per wrapper paginati:
     *
     * if (dto == null || dto.getResults() == null) {
     *     return List.of();
     * }
     *
     * return dto.getResults().stream()
     *     .map(this::toDomain)
     *     .toList();
     */
    default List<ProducerKeychain> toDomainList(it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys dto) {
        return List.of();
    }
}
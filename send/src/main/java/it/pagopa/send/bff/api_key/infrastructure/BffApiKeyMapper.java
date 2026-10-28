package it.pagopa.send.bff.api_key.infrastructure;

import it.pagopa.send.common.api_key.domain.ApiKey;
import it.pagopa.send.common.infrastructure.config.SendMapperConfig;
import it.pagopa.send.generated.openapi.clients.bff.apikeys.model.BffRequestNewApiKey;
import it.pagopa.send.generated.openapi.clients.bff.apikeys.model.BffResponseNewApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Unico punto del canale BFF che conosce sia il dominio {@link ApiKey} sia i DTO OpenAPI delle API Key. Il valore della
 * chiave presente nella risposta di creazione è un segreto e non viene copiato nel dominio.
 */
@Mapper(config = SendMapperConfig.class)
public interface BffApiKeyMapper {

    default BffRequestNewApiKey toBffRequest(String name) {
        return new BffRequestNewApiKey().name(name).groups(List.of());
    }

    @Mapping(target = "apiKeyId", source = "response.id")
    @Mapping(target = "name", source = "name")
    ApiKey toDomain(BffResponseNewApiKey response, String name);
}

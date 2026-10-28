package it.pagopa.send.bff.api_key.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.api_key.application.ApiKeyGateway;
import it.pagopa.send.common.api_key.domain.ApiKey;
import it.pagopa.send.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementazione BFF di {@link ApiKeyGateway}: le API Key sono create per l'ente del token BFF
 * ({@code bff.api.bearer-token}).
 */
@Service
@RequiredArgsConstructor
public class BffApiKeyGateway implements ApiKeyGateway {

    private final ApiKeyRestClient restClient;
    private final BffApiKeyMapper mapper;
    private final EntityStore entityStore;

    @Override
    public ApiKey createApiKey(String name) {
        ApiKey apiKey = restClient.create(mapper.toBffRequest(name))
                .withoutPolling()
                .map(response -> mapper.toDomain(response, name))
                .get();
        entityStore.upsert(apiKey);
        return apiKey;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

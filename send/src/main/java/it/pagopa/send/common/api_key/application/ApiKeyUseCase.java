package it.pagopa.send.common.api_key.application;

import it.pagopa.send.common.api_key.domain.ApiKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Casi d'uso sulle API Key dell'ente mittente. Puro pass-through verso {@link ApiKeyGateway}.
 */
@Service
@RequiredArgsConstructor
public class ApiKeyUseCase {

    private final ApiKeyGateway apiKeyGateway;

    /**
     * Genera una API Key attiva e senza gruppi.
     */
    public ApiKey createApiKey(String name) {
        return apiKeyGateway.createApiKey(name);
    }
}

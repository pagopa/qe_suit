package it.pagopa.send.stream.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.streamlegacy.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Client delle API pn-stream legacy (v1.0, v2.3-v2.9), con base URL e api-key di
 * {@link StreamLegacyRequestSpecFactory}.
 */
@Configuration
public class StreamLegacyClientConfig {

    @Bean("streamLegacyApiClient")
    public ApiClient streamLegacyApiClient(StreamLegacyRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(requestSpecFactory::create));
    }
}

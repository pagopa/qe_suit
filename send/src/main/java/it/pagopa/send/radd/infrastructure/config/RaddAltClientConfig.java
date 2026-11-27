package it.pagopa.send.radd.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.radd.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Client generato delle API B2B RADD ({@code pn-radd-alt-external.yaml}).
 */
@Configuration
public class RaddAltClientConfig {

    @Bean("raddApiClient")
    public ApiClient raddApiClient(RaddRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(requestSpecFactory::create));
    }
}

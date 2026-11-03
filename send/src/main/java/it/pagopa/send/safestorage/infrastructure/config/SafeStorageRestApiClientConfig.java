package it.pagopa.send.safestorage.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.safestorage.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SafeStorageRestApiClientConfig {
    @Bean("safeStorageApiClient")
    public ApiClient safeStorageApiClient(SafeStorageRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(requestSpecFactory::create));
    }
}
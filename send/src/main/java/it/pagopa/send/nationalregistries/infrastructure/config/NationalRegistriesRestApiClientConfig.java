package it.pagopa.send.nationalregistries.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.nationalregistries.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NationalRegistriesRestApiClientConfig {

    @Bean("nationalRegistriesApiClient")
    public ApiClient nationalRegistriesApiClient(NationalRegistriesRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(requestSpecFactory::create));
    }
}

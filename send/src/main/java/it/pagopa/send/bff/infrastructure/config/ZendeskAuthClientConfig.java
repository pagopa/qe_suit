package it.pagopa.send.bff.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.zendeskauth.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZendeskAuthClientConfig {

    @Bean
    public ApiClient zendeskAuthApiClient(ZendeskAuthRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create));
    }
}

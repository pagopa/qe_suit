package it.pagopa.send.stream.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.stream.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamClientConfig {

    @Bean("streamApiClient")
    public ApiClient streamApiClient(StreamRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(requestSpecFactory::create));
    }
}

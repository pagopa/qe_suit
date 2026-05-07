package it.pagopa.interop.infrastructure.client.config;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {


    @Bean
    public ApiClient bearerTokenApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBearerToken(System.getenv("BEARER_TOKEN"));
        return apiClient;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

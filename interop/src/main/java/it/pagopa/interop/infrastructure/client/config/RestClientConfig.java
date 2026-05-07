package it.pagopa.interop.infrastructure.client.config;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.infrastructure.client.auth.bearer.BearerAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public ApiClient bearerTokenApiClient(BearerAuthProvider bearerAuthProvider, @Value("${interop.api.base-url.bff}") String basePath) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerAuthProvider::getToken);
        return apiClient;
    }

    @Bean
    public EservicesApi eserviceClient(ApiClient apiClient) {
        return new EservicesApi(apiClient);
    }

    @Bean
    public AgreementsApi agreementsApi(ApiClient apiClient) {
        return new AgreementsApi(apiClient);
    }

    @Bean
    public PurposesApi purposesApi(ApiClient apiClient) {
        return new PurposesApi(apiClient);
    }
}
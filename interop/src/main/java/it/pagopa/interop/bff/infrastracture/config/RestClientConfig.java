package it.pagopa.interop.bff.infrastracture.config;

import it.pagopa.interop.common.infrastructure.http.interceptor.HttpLoggingInterceptor;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.*;
import it.pagopa.interop.bff.infrastracture.auth.bearer.BearerAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public org.springframework.web.client.RestClient bffRestClient() {
        return org.springframework.web.client.RestClient.builder()
                .requestFactory(HttpLoggingInterceptor.bufferingFactory())
                .requestInterceptor(new HttpLoggingInterceptor())
                .build();
    }

    @Bean
    public ApiClient bearerTokenApiClient(BearerAuthProvider bearerAuthProvider,
                                          @Value("${interop.api.base-url.bff}") String basePath,
                                          org.springframework.web.client.RestClient bffRestClient) {
        ApiClient apiClient = new ApiClient(bffRestClient);
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

    @Bean
    public ClientsApi clientsApi(ApiClient apiClient) {
        return new ClientsApi(apiClient);
    }

    @Bean
    public ProducerKeychainApi  producerKeychainApi(ApiClient apiClient) {
        return new ProducerKeychainApi(apiClient);
    }
}
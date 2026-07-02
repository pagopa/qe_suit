package it.pagopa.interop.new_arch.bff.infrastructure.config;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.new_arch.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.new_arch.common.infrastructure.interceptor.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiClientConfig {
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

}

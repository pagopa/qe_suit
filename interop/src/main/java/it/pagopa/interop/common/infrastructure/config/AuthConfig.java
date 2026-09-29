package it.pagopa.interop.common.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.interop.generated.openapi.clients.auth.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Value("${interop.auth.oauth.server.base-url}")
    private String basePath;

    @Bean
    public ApiClient authApiClient() {
        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .addFilter(new HttpLoggingFilter()));

        return ApiClient.api(apiConfig);
    }
}
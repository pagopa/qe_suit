package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.generated.openapi.clients.externalregistries.ApiClient;
import it.pagopa.send.generated.openapi.clients.externalregistries.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.externalregistries.api.InfoPaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalRegistriesClientConfig {

    @Value("${external-registries.api.base-url}")
    private String basePath;

    @Bean
    public InfoPaApi infoPaApi() {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .setConfig(RestAssuredConfig.config()
                                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson()))));

        return ApiClient.api(config).infoPa();
    }
}

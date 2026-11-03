package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.generated.openapi.clients.mandate.ApiClient;
import it.pagopa.send.generated.openapi.clients.mandate.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.mandate.api.MandateApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Client delle deleghe del BFF. Il bearer non è nel bean: ogni chiamata usa il session token dell'utente che la fa
 * (delegante o delegato), aggiunto da {@code BffDelegationRestClient}.
 */
@Configuration
public class BffMandateClientConfig {

    @Value("${bff.api.base-url}")
    private String basePath;

    @Bean
    public MandateApi mandateApi() {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .setConfig(RestAssuredConfig.config()
                                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson()))));

        return ApiClient.api(config).mandate();
    }
}

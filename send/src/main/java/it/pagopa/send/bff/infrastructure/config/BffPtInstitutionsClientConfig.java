package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.generated.openapi.clients.bff.ApiClient;
import it.pagopa.send.generated.openapi.clients.bff.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.bff.api.PtInstitutionsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Client degli enti deleganti del Partner Tecnologico. Il bearer non è nel bean: ogni chiamata usa il session token
 * del PT, aggiunto da {@code BffPtInstitutionsRestClient}.
 */
@Configuration
public class BffPtInstitutionsClientConfig {

    // i percorsi del client BFF non includono /bff
    @Value("${bff.api.base-url}")
    private String basePath;

    @Bean
    public PtInstitutionsApi ptInstitutionsApi() {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .setConfig(RestAssuredConfig.config()
                                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson()))));

        return ApiClient.api(config).ptInstitutions();
    }
}

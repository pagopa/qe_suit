package it.pagopa.send.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.generated.openapi.clients.gpd.ApiClient;
import it.pagopa.send.generated.openapi.clients.gpd.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.gpd.api.DebtPositionsApiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GpdClientConfig {

    @Value("${gpd.api.base-url}")
    private String basePath;

    @Value("${gpd.api.subscription-key}")
    private String subscriptionKey;

    @Bean
    public DebtPositionsApiApi debtPositionsApiApi() {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .setConfig(RestAssuredConfig.config()
                                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson())))
                        .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey));

        return ApiClient.api(config).debtPositionsApi();
    }

}

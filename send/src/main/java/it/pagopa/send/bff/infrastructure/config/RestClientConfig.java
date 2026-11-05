package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.generated.openapi.clients.bff.ApiClient;
import it.pagopa.send.generated.openapi.clients.bff.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.bff.api.NotificationSentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Value("${bff.api.base-url}")
    private String basePath;

    @Value("${bff.api.bearer-token}")
    private String bearerToken;

    /**
     * Client BFF con autenticazione per utenza: il {@code reqSpecSupplier} viene risolto a ogni
     * costruzione di operazione, cosi' il token segue l'utenza in sessione sul thread corrente.
     * Usato dai contract test di autorizzazione; il bean {@link #notificationSentApi()} resta
     * invariato per i flussi preesistenti, che usano il token statico del profilo.
     */
    @Bean
    public ApiClient bffApiClient(BffRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create));
    }

    @Bean
    public NotificationSentApi notificationSentApi() {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .setConfig(RestAssuredConfig.config()
                                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson())))
                        .addHeader("Authorization", "Bearer " + bearerToken));

        return ApiClient.api(config).notificationSent();
    }

}

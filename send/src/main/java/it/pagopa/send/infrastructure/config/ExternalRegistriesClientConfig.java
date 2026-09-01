package it.pagopa.send.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.externalregistries.ApiClient;
import it.pagopa.send.generated.openapi.clients.externalregistries.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.externalregistries.api.InfoPaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * L'api-key per /ext-registry-b2b/pa/v1/groups è unica per PA e per ambiente (a differenza,
     * es., della subscription-key di GPD): non può quindi essere un singolo valore iniettato nel
     * bean del client, va risolta per {@link Tenant} al momento
     * della chiamata. Bind diretto del prefisso su una Map, valorizzata da {@code pa.groups-api-key}
     * negli application-{profilo}.yaml (chiave = nome della costante enum Tenant).
     */
    @Bean
    @ConfigurationProperties(prefix = "pa.groups-api-key")
    public Map<String, String> paGroupsApiKeys() {
        return new HashMap<>();
    }
}

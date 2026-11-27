package it.pagopa.send.radd.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.generated.openapi.clients.raddregistry.ApiClient;
import it.pagopa.send.generated.openapi.clients.raddregistry.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * Client generato delle API B2B RADD di censimento dei punti di ritiro ({@code pn-radd-alt-registry-v2-external.yaml}).
 * <p>
 * Lo spec dichiara solo {@code bearerAuth}: il JWT statico dell'operatore RADD ({@code radd.jwt.raddista-1}, nei
 * secrets) e' lo stesso delle API {@code pn-radd-alt-external}, e va rinnovato alla scadenza. L'header
 * {@code x-pagopa-pn-cx-id} non viene inviato: lo valorizza l'authorizer del gateway a partire dal JWT.
 * <p>
 * Base URL dedicata ({@code radd.registry.api.base-url}, in {@code application-<ambiente>.yaml}): default vuoto,
 * cosi' il bean esiste in ogni contesto e la mancanza si scopre solo alla prima chiamata.
 */
@Configuration
public class RaddAltRegistryClientConfig {

    static final String BASE_URL_PROPERTY = "radd.registry.api.base-url";
    static final String JWT_PROPERTY = "radd.jwt.raddista-1";

    @Value("${" + BASE_URL_PROPERTY + ":}")
    private String baseUrl;

    @Value("${" + JWT_PROPERTY + ":}")
    private String jwt;

    @Bean("raddRegistryApiClient")
    public ApiClient raddRegistryApiClient() {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(this::requestSpec));
    }

    private RequestSpecBuilder requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(required(baseUrl, BASE_URL_PROPERTY))
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addHeader("Authorization", "Bearer " + required(jwt, JWT_PROPERTY))
                .addFilter(new HttpLoggingFilter());
    }

    private static String required(String value, String property) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Property " + property + " non valorizzata: necessaria per le chiamate al registry RADD "
                            + "(base-url in application-<ambiente>.yaml, jwt nei secrets)");
        }
        return value;
    }
}

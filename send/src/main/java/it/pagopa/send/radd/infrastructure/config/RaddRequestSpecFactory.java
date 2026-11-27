package it.pagopa.send.radd.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.generated.openapi.clients.radd.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * Request specification delle API B2B RADD ({@code pn-radd-alt-external}). Lo spec dichiara solo {@code bearerAuth}
 * (JWT RS256 dell'operatore RADD): il token e' statico, letto da {@code radd.jwt.raddista-1}, e va rinnovato alla scadenza.
 * <p>
 * L'header {@code uid} (obbligatorio su tutte le operazioni tranne il download) identifica l'operatore dello
 * sportello: non e' un segreto, di default vale {@value #DEFAULT_UID} come in pn-b2b-client ed e' sovrascrivibile con
 * {@code radd.api.uid}. E' fisso e non e' oggetto di fuzzing, il framework muta solo body, path e query.
 */
@Component
public class RaddRequestSpecFactory {

    static final String UID_HEADER = "uid";
    static final String DEFAULT_UID = "1234556";

    /**
     * Base URL e JWT hanno default vuoto: il bean esiste in ogni contesto (anche Cucumber) e la mancanza si scopre
     * solo alla prima chiamata RADD.
     */
    @Value("${radd.api.base-url:}")
    private String baseUrl;

    @Value("${radd.jwt.raddista-1:}")
    private String jwt;

    @Value("${radd.api.uid:" + DEFAULT_UID + "}")
    private String uid;

    public RequestSpecBuilder create() {
        return new RequestSpecBuilder()
                .setBaseUri(required(baseUrl, "radd.api.base-url"))
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addHeader("Authorization", "Bearer " + required(jwt, "radd.jwt.raddista-1"))
                .addHeader(UID_HEADER, required(uid, "radd.api.uid"))
                .addFilter(new HttpLoggingFilter());
    }

    private static String required(String value, String property) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Property " + property + " non valorizzata: necessaria per le chiamate RADD "
                            + "(base-url in application-<ambiente>.yaml, jwt nei secrets)");
        }
        return value;
    }
}

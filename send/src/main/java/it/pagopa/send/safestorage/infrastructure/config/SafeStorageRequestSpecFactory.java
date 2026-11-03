package it.pagopa.send.safestorage.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.generated.openapi.clients.safestorage.JacksonObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

@Component
public class SafeStorageRequestSpecFactory {
    @Value("${safestorage.api.base-url}") private String baseUrl;
    @Value("${safestorage.api.api-key}") private String apiKey;

    public RequestSpecBuilder create() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addHeader("x-api-key", apiKey)
                .addFilter(new HttpLoggingFilter());
    }
}
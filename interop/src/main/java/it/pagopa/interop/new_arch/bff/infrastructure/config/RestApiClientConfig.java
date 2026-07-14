package it.pagopa.interop.new_arch.bff.infrastructure.config;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.cucumber.spring.ScenarioScope;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.new_arch.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.TestContext;
import it.pagopa.interop.new_arch.common.infrastructure.http.HttpLoggingFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

@Configuration
public class RestApiClientConfig {

    @Value("${interop.api.base-url.bff}")
    private String basePath;

    @Value("${interop.api.openapi-url.bff}")
    private String openApiSpecUrl;

    @Getter
    @RequiredArgsConstructor
    private enum ValidationKey {
        INVALID_REQUEST_BODY("validation.request.body.schema.invalid"),
        UNKNOWN_RESPONSE_STATUS("validation.response.status.unknown"),
        UNEXPECTED_RESPONSE_BODY("validation.response.body.unexpected");

        private final String key;
    }

    @Bean
    @ScenarioScope
    public ApiClient apiClient(BearerAuthProvider bearerAuthProvider, TestContext testContext) {
        return switch (testContext.getCurrentTestKind()) {
            case CONTRACT -> contractApiClient(bearerAuthProvider);
            case FLOW -> flowApiClient(bearerAuthProvider);
        };
    }

    private ApiClient contractApiClient(BearerAuthProvider bearerAuthProvider) {
        OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(openApiSpecUrl)
                .withLevelResolver(LevelResolver.create()
                        .withLevel(ValidationKey.INVALID_REQUEST_BODY.getKey(), ValidationReport.Level.IGNORE)
                        .withLevel(ValidationKey.UNKNOWN_RESPONSE_STATUS.getKey(), ValidationReport.Level.WARN)
                        .withLevel(ValidationKey.UNEXPECTED_RESPONSE_BODY.getKey(), ValidationReport.Level.ERROR)
                        .build())
                .build();

        Filter openApiFilter = new OpenApiValidationFilter(validator);

        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> createBaseSpecBuilder(bearerAuthProvider)
                        .addFilter(openApiFilter)
                );

        return ApiClient.api(apiConfig);
    }

    private ApiClient flowApiClient(BearerAuthProvider bearerAuthProvider) {
        Filter macroStatusCodeFilter = (requestSpec, responseSpec, ctx) -> {
            var response = ctx.next(requestSpec, responseSpec);
            int code = response.getStatusCode();
            if ((code >= 200 && code < 300) || (code >= 400 && code < 500)) {
                return response;
            }
            throw new AssertionError("Test di Flusso interrotto! Errore server: " + code);
        };

        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> createBaseSpecBuilder(bearerAuthProvider)
                        .addFilter(macroStatusCodeFilter)
                );

        return ApiClient.api(apiConfig);
    }

    private RequestSpecBuilder createBaseSpecBuilder(BearerAuthProvider bearerAuthProvider) {
        return new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(jackson())))
                .setAuth(RestAssured.oauth2(bearerAuthProvider.getToken()))
                .addFilter(new HttpLoggingFilter());
    }
}
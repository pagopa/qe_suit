package it.pagopa.interop.bff.infrastructure.config;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.infrastructure.context.CurrentTestKind;
import it.pagopa.interop.common.infrastructure.context.CurrentUserSession;
import it.pagopa.interop.common.infrastructure.http.HttpLoggingFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

@Configuration
public class BffRestApiClientConfig {

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
    public ApiClient apiClient(
            BearerAuthProvider bearerAuthProvider,
            ObjectProvider<CurrentTestKind> testKindProvider,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider
    ) {
        Filter contractTestFilter = createContractTestFilter();
        Filter businessTestFilter = createBusinessTestFilter();

        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> {
                    CurrentTestKind currentTestKind = testKindProvider.getObject();
                    CurrentUserSession currentUserSession = currentUserSessionProvider.getObject();

                    String token = bearerAuthProvider.getToken(
                            currentUserSession.getUser(),
                            currentUserSession.getTenant()
                    );

                    RequestSpecBuilder builder = createBaseSpecBuilder(token);

                    switch (currentTestKind.getCurrentTestKind()) {
                        case CONTRACT -> builder.addFilter(contractTestFilter);
                        case FLOW -> builder.addFilter(businessTestFilter);
                    }

                    return builder;
                });

        return ApiClient.api(apiConfig);
    }

    private Filter createContractTestFilter() {
        String apiBasePath = URI.create(basePath).getPath();

        OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(openApiSpecUrl)
                .withBasePathOverride(apiBasePath)
                .withLevelResolver(LevelResolver.create()
                        .withLevel(ValidationKey.INVALID_REQUEST_BODY.getKey(), ValidationReport.Level.IGNORE)
                        .withLevel(ValidationKey.UNKNOWN_RESPONSE_STATUS.getKey(), ValidationReport.Level.WARN)
                        .withLevel(ValidationKey.UNEXPECTED_RESPONSE_BODY.getKey(), ValidationReport.Level.ERROR)
                        .build())
                .build();

        return new OpenApiValidationFilter(validator);
    }

    private Filter createBusinessTestFilter() {
        return (requestSpec, responseSpec, ctx) -> {
            var response = ctx.next(requestSpec, responseSpec);
            int code = response.getStatusCode();
            if ((code >= 200 && code < 300) || (code >= 400 && code < 500)) {
                return response;
            }
            throw new AssertionError("Test di Flusso interrotto! Errore server: " + code);
        };
    }

    private RequestSpecBuilder createBaseSpecBuilder(String bearerToken) {
        return new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(
                        config().objectMapperConfig(
                                objectMapperConfig().defaultObjectMapper(jackson())
                        )
                )
                .addFilter(new HttpLoggingFilter())
                .addHeader("Authorization", "Bearer " + bearerToken);
    }
}
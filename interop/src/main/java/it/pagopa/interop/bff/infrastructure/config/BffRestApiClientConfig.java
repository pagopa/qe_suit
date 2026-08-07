package it.pagopa.interop.bff.infrastructure.config;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.filter.Filter;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class BffRestApiClientConfig {

    @Value("${interop.api.base-url.bff}")
    private String basePath;

    @Value("${interop.api.openapi-url.bff}")
    private String openApiSpecUrl;

    @Getter
    @RequiredArgsConstructor
    private enum ValidationKey {
        REQUEST_VALIDATION("validation.request"),
        UNKNOWN_RESPONSE_STATUS("validation.response.status.unknown"),
        UNEXPECTED_CONTENT_TYPE("validation.response.contentType.notAllowed"),
        UNEXPECTED_RESPONSE_BODY("validation.response.body.unexpected");

        private final String key;
    }

    @Bean
    public ApiClient apiClient(BffRequestSpecFactory requestSpecFactory) {
        ApiClient.Config apiConfig = ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create);

        return ApiClient.api(apiConfig);
    }

    @Bean("contractTestFilter")
    public Filter contractTestFilter() {
        String apiBasePath = URI.create(basePath).getPath();

        OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(openApiSpecUrl)
                .withBasePathOverride(apiBasePath)
                .withLevelResolver(
                        LevelResolver.create()
                                .withLevel(
                                        ValidationKey.REQUEST_VALIDATION.getKey(),
                                        ValidationReport.Level.IGNORE
                                )
                                .withLevel(
                                        ValidationKey.UNKNOWN_RESPONSE_STATUS.getKey(),
                                        ValidationReport.Level.WARN
                                )
                                .withLevel(
                                        ValidationKey.UNEXPECTED_RESPONSE_BODY.getKey(),
                                        ValidationReport.Level.ERROR
                                )
                                .withLevel(
                                        ValidationKey.UNEXPECTED_CONTENT_TYPE.getKey(),
                                        ValidationReport.Level.WARN
                                )
                                .build()
                )
                .build();

        return new OpenApiValidationFilter(validator);
    }

    @Bean("businessTestFilter")
    public Filter businessTestFilter() {
        return (requestSpec, responseSpec, ctx) -> {
            var response = ctx.next(requestSpec, responseSpec);

            int code = response.getStatusCode();

            if ((code >= 200 && code < 300)
                    || (code >= 400 && code < 500)) {
                return response;
            }

            throw new AssertionError(
                    "Test di Flusso interrotto! Errore server: " + code
            );
        };
    }
}
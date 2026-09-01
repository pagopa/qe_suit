package it.pagopa.infrastructure.http.restassured.contract;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.filter.Filter;

import java.net.URI;

public final class ContractValidationFilterFactory {

    private static final String REQUEST_VALIDATION = "validation.request";
    private static final String UNKNOWN_RESPONSE_STATUS = "validation.response.status.unknown";
    private static final String UNEXPECTED_RESPONSE_BODY = "validation.response.body.unexpected";
    private static final String UNEXPECTED_CONTENT_TYPE = "validation.response.contentType.notAllowed";

    private ContractValidationFilterFactory() {
    }

    public static Filter create(String baseUrl, String openApiSpecUrl) {
        String apiBasePath = URI.create(baseUrl).getPath();

        OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createFor(openApiSpecUrl)
                .withBasePathOverride(apiBasePath)
                .withLevelResolver(levelResolver())
                .build();

        return new OpenApiValidationFilter(validator);
    }

    private static LevelResolver levelResolver() {
        return LevelResolver.create()
                .withLevel(REQUEST_VALIDATION, ValidationReport.Level.IGNORE)
                .withLevel(UNKNOWN_RESPONSE_STATUS, ValidationReport.Level.WARN)
                .withLevel(UNEXPECTED_RESPONSE_BODY, ValidationReport.Level.ERROR)
                .withLevel(UNEXPECTED_CONTENT_TYPE, ValidationReport.Level.WARN)
                .build();
    }
}

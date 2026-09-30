package it.pagopa.send.bff.infrastructure.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;

import it.pagopa.send.common.kernel.context.CurrentUserSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.send.generated.openapi.clients.internal.template.JacksonObjectMapper.jackson;

@Component
public class TemplateRequestSpecFactory {

    private final ObjectProvider<TestContext> testKindProvider;
    private final TestPolicyFilterResolver testPolicyFilterResolver;

    @Value("${template.api.base-url}")
    private String basePath;
    @Value("${template.api.base-url}")
    private String token;

    public TemplateRequestSpecFactory(
            ObjectProvider<TestContext> testKindProvider,
            TestPolicyFilterResolver testPolicyFilterResolver
    ) {
        this.testKindProvider = testKindProvider;
        this.testPolicyFilterResolver = testPolicyFilterResolver;
    }

    public RequestSpecBuilder create() {
        TestContext testContext = testKindProvider.getObject();
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(
                        config().objectMapperConfig(
                                objectMapperConfig()
                                        .defaultObjectMapper(jackson())
                        )
                );

        TestKind testKind = testContext.getCurrentTestKind();
        builder.addFilter(testPolicyFilterResolver.resolve(testKind));
        
        // HttpLoggingFilter deve essere ULTIMO nella catena (primo eseguito)
        // per loggare la response PRIMA che i validatori la elaborino
        builder.addFilter(new HttpLoggingFilter());
        return builder;
    }

    public RequestSpecification given() {
        return RestAssured.given()
                .spec(create().build());
    }
}
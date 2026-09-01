package it.pagopa.interop.bff.infrastructure.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.application.context.TestContext;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

@Component
public class BffRequestSpecFactory {

    private final BearerAuthProvider bearerAuthProvider;
    private final ObjectProvider<TestContext> testKindProvider;
    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final TestPolicyFilterResolver testPolicyFilterResolver;

    @Value("${interop.api.base-url.bff}")
    private String basePath;

    public BffRequestSpecFactory(
            BearerAuthProvider bearerAuthProvider,
            ObjectProvider<TestContext> testKindProvider,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            TestPolicyFilterResolver testPolicyFilterResolver
    ) {
        this.bearerAuthProvider = bearerAuthProvider;
        this.testKindProvider = testKindProvider;
        this.currentUserSessionProvider = currentUserSessionProvider;
        this.testPolicyFilterResolver = testPolicyFilterResolver;
    }

    public RequestSpecBuilder create() {
        TestContext testContext = testKindProvider.getObject();
        CurrentUserSession currentUserSession = currentUserSessionProvider.getObject();

        String token = bearerAuthProvider.getToken(
                currentUserSession.getUser(),
                currentUserSession.getTenant()
        );

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(
                        config().objectMapperConfig(
                                objectMapperConfig()
                                        .defaultObjectMapper(jackson())
                        )
                )
                .addHeader("Authorization", "Bearer " + token);

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
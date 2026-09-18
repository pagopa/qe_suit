package it.pagopa.interop.m2m.v3.infrastructure.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.interop.m2m.kernel.context.CurrentApiClientSession;
import it.pagopa.interop.m2m.v3.infrastructure.security.DPoPAuthenticationFilter;
import it.pagopa.interop.m2m.v3.infrastructure.security.M2MAuthSession;
import it.pagopa.interop.m2m.v3.infrastructure.security.M2Mv3AuthSessionProvider;
import it.pagopa.kernel.security.DPoPProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

@Component
@RequiredArgsConstructor
public class M2Mv3RequestSpecFactory {

    @Value("${interop.api.base-url.m2m.v3}")
    private String basePath;

    private final ObjectProvider<TestContext> testKindProvider;
    private final ObjectProvider<CurrentApiClientSession> currentApiClientSessions;

    private final TestPolicyFilterResolver testPolicyFilterResolver;
    private final M2Mv3AuthSessionProvider authSessionProvider;
    private final DPoPProofService dPoPProofService;

    public RequestSpecBuilder create() throws NoSuchAlgorithmException, JsonProcessingException {

        TestContext testContext = testKindProvider.getObject();
        CurrentApiClientSession currentSession = currentApiClientSessions.getObject();

        M2MAuthSession authSession =
                authSessionProvider.getOrCreate(
                        currentSession.getTenant(),
                        currentSession.getRole()
                );

        RequestSpecBuilder builder =
                new RequestSpecBuilder()
                        .setBaseUri(basePath)
                        .setConfig(
                                config().objectMapperConfig(
                                        objectMapperConfig()
                                                .defaultObjectMapper(jackson())
                                )
                        );

        builder.addFilter(
                new DPoPAuthenticationFilter(
                        authSession.accessToken(),
                        authSession.dpopKeyPair(),
                        dPoPProofService
                )
        );

        TestKind testKind = testContext.getCurrentTestKind();

        builder.addFilter(testPolicyFilterResolver.resolve(testKind));
        builder.addFilter(new HttpLoggingFilter());

        return builder;
    }

    public RequestSpecification given() throws NoSuchAlgorithmException, JsonProcessingException {
        return RestAssured.given()
                .spec(create().build());
    }
}
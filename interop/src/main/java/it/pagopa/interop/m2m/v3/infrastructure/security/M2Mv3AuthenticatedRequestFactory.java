package it.pagopa.interop.m2m.v3.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.kernel.security.DPoPProofService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

@Component
public class M2Mv3AuthenticatedRequestFactory {

    @Value("${interop.api.base-url.m2m.v3}")
    private String basePath;

    private final ObjectProvider<TestContext> testKindProvider;
    private final ObjectProvider<CurrentM2MSession> currentApiClientSessions;
    private final TestPolicyFilterResolver testPolicyFilterResolver;
    private final M2Mv3AccessTokenProvider accessTokenProvider;
    private final DPoPProofService dPoPProofService;

    public M2Mv3AuthenticatedRequestFactory(
            ObjectProvider<TestContext> testKindProvider,
            ObjectProvider<CurrentM2MSession> currentApiClientSessions,
            @Qualifier("m2mV3TestPolicyFilterResolver") TestPolicyFilterResolver testPolicyFilterResolver,
            M2Mv3AccessTokenProvider accessTokenProvider,
            DPoPProofService dPoPProofService
    ) {
        this.testKindProvider = testKindProvider;
        this.currentApiClientSessions = currentApiClientSessions;
        this.testPolicyFilterResolver = testPolicyFilterResolver;
        this.accessTokenProvider = accessTokenProvider;
        this.dPoPProofService = dPoPProofService;
    }

    public RequestSpecBuilder create() throws NoSuchAlgorithmException, JsonProcessingException {

        TestContext testContext = testKindProvider.getObject();
        CurrentM2MSession currentSession = currentApiClientSessions.getObject();

        M2Mv3AccessTokenContext authContext =
                accessTokenProvider.getOrCreate(
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
                new it.pagopa.infrastructure.http.restassured.DPoPAuthenticationFilter(
                        authContext.accessToken(),
                        authContext.dpopKeyPair(),
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
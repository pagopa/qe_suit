package it.pagopa.interop.m2m.v3.infrastructure.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.domain.InteropActor;
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
    private final ObjectProvider<AuthSession> currentApiClientSessions;
    private final TestPolicyFilterResolver testPolicyFilterResolver;
    private final M2Mv3AccessTokenProvider accessTokenProvider;
    private final DPoPProofService dPoPProofService;

    public M2Mv3AuthenticatedRequestFactory(
            ObjectProvider<TestContext> testKindProvider,
            ObjectProvider<AuthSession> currentApiClientSessions,
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

    public RequestSpecBuilder create() {
        try {
            return createInternal();
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            throw new IllegalStateException("Unable to create M2M v3 request specification", e);
        }
    }

    private RequestSpecBuilder createInternal() throws NoSuchAlgorithmException, JsonProcessingException {

        TestContext testContext = testKindProvider.getObject();
        AuthSession currentSession = currentApiClientSessions.getObject();

        InteropActor actor = currentSession.getActor();
        if (!(actor instanceof Client client)) {
            throw new IllegalStateException("M2M v3 session does not contain a client");
        }
        M2Mv3AccessTokenContext authContext = accessTokenProvider.create(client);

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

    public RequestSpecification given() {
        return RestAssured.given()
                .spec(create().build());
    }
}
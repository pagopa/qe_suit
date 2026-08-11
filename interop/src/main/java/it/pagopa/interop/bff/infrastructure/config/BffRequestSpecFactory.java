package it.pagopa.interop.bff.infrastructure.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.kernel.context.CurrentTestKind;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.infrastructure.restassured.HttpLoggingFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static it.pagopa.interop.generated.openapi.clients.bff.JacksonObjectMapper.jackson;

@Component
public class BffRequestSpecFactory {

    private final BearerAuthProvider bearerAuthProvider;
    private final ObjectProvider<CurrentTestKind> testKindProvider;
    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final Filter contractTestFilter;
    private final Filter businessTestFilter;

    @Value("${interop.api.base-url.bff}")
    private String basePath;

    public BffRequestSpecFactory(
            BearerAuthProvider bearerAuthProvider,
            ObjectProvider<CurrentTestKind> testKindProvider,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            @Qualifier("contractTestFilter") Filter contractTestFilter,
            @Qualifier("businessTestFilter") Filter businessTestFilter
    ) {
        this.bearerAuthProvider = bearerAuthProvider;
        this.testKindProvider = testKindProvider;
        this.currentUserSessionProvider = currentUserSessionProvider;
        this.contractTestFilter = contractTestFilter;
        this.businessTestFilter = businessTestFilter;
    }

    public RequestSpecBuilder create() {
        CurrentTestKind currentTestKind = testKindProvider.getObject();
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

        switch (currentTestKind.getCurrentTestKind()) {
            case CONTRACT -> builder.addFilter(contractTestFilter);
            case FLOW -> builder.addFilter(businessTestFilter);
        }
        
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
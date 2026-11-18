package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.generated.openapi.clients.zendeskauth.JacksonObjectMapper;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * Costruisce la request specification del client Zendesk Auth risolvendo il bearer token
 * dell'utenza, allo stesso modo di {@link BffRequestSpecFactory}: letto da
 * {@link CurrentUserSession} alla costruzione di ogni operazione.
 */
@Component
public class ZendeskAuthRequestSpecFactory {

    private final CurrentUserSession currentUserSession;
    private final DynamicUserTokenProvider tokenProvider;

    @Value("${zendesk.api.base-url}")
    private String basePath;

    public ZendeskAuthRequestSpecFactory(
            CurrentUserSession currentUserSession,
            DynamicUserTokenProvider tokenProvider
    ) {
        this.currentUserSession = currentUserSession;
        this.tokenProvider = tokenProvider;
    }

    public RequestSpecBuilder create() {
        return new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addHeader(
                        "Authorization",
                        "Bearer " + tokenProvider.requireToken(currentUserSession.getUser().getSessionTokenKey())
                )
                .addFilter(new HttpLoggingFilter());
    }
}

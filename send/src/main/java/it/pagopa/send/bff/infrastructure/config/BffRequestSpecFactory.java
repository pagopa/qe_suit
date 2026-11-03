package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.generated.openapi.clients.bff.JacksonObjectMapper;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * Costruisce la request specification del client BFF risolvendo il bearer token dell'utenza
 * corrente, invece del token statico di {@code bff.api.bearer-token}.
 * <p>
 * Viene invocata dal {@code reqSpecSupplier} del client generato, cioe' alla costruzione di ogni
 * operazione: legge {@link CurrentUserSession} (un {@code ThreadLocal}) in quel momento e congela
 * il token nell'operazione. Per questo l'autenticazione deve precedere la creazione
 * dell'operazione, e ogni caso di test deve costruirsi la propria.
 */
@Component
public class BffRequestSpecFactory {

    private final CurrentUserSession currentUserSession;
    private final DynamicUserTokenProvider tokenProvider;

    @Value("${bff.api.base-url}")
    private String basePath;

    public BffRequestSpecFactory(
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
                        "Bearer " + tokenProvider.requireToken(currentUserSession.getUser().getUsername())
                )
                .addFilter(new HttpLoggingFilter());
    }
}

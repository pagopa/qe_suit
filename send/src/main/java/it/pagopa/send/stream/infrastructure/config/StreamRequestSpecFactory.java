package it.pagopa.send.stream.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.infrastructure.config.UsersProperties;
import it.pagopa.send.generated.openapi.clients.stream.JacksonObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * pn-stream (API B2B avanzamento notifiche) e' esposto dallo stesso gateway B2B di "delivery":
 * l'autenticazione e' la stessa api-key ({@code x-api-key}) dell'ente mittente corrente
 * (vedi {@link it.pagopa.send.bff.delivery.infrastructure.config.BffDeliveryRequestSpecFactory}).
 * <p>
 * La base URL e' {@code stream.api.base-url} se definita, altrimenti ricade su
 * {@code delivery.api.base-url}.
 */
@Component
public class StreamRequestSpecFactory {

    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final UsersProperties usersProperties;

    @Value("${stream.api.base-url:${delivery.api.base-url}}")
    private String baseUrl;

    public StreamRequestSpecFactory(
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            UsersProperties usersProperties
    ) {
        this.currentUserSessionProvider = currentUserSessionProvider;
        this.usersProperties = usersProperties;
    }

    public RequestSpecBuilder create() {
        User sender = currentUserSessionProvider.getObject().getUser();
        if (sender == null) {
            throw new IllegalStateException("Nessuna utenza in sessione per le chiamate pn-stream");
        }
        String apiKey = usersProperties.findByUsername(sender.getUsername())
                .map(UsersProperties.UserEntry::deliveryApiKey)
                .orElse(null);
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Nessuna api-key delivery configurata per l'utenza " + sender.getUsername()
                            + " (campo delivery-api-key in users/application-users-<ambiente>.yaml)");
        }

        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addHeader("x-api-key", apiKey)
                .addFilter(new HttpLoggingFilter());
    }
}

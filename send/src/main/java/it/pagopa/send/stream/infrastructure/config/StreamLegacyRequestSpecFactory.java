package it.pagopa.send.stream.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.infrastructure.config.UsersProperties;
import it.pagopa.send.generated.openapi.clients.streamlegacy.JacksonObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * Richieste verso le API pn-stream legacy (v1.0, v2.3-v2.9). pn-stream e' esposto dallo stesso
 * gateway B2B di "delivery": l'autenticazione e' la stessa api-key ({@code x-api-key}) dell'ente
 * mittente corrente (vedi {@link it.pagopa.send.bff.delivery.infrastructure.config.BffDeliveryRequestSpecFactory}).
 * <p>
 * La base URL e' {@code stream.api.base-url} se definita, altrimenti ricade su
 * {@code delivery.api.base-url}.
 * <p>
 * L'api-key e' aggiunta da un filtro eseguito dopo {@link HttpLoggingFilter}, che scrive la richiesta
 * prima di passarla al filtro successivo: richieste e risposte restano nei log, la chiave no.
 */
@Component
public class StreamLegacyRequestSpecFactory {

    private static final String API_KEY_HEADER = "x-api-key";

    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final UsersProperties usersProperties;

    @Value("${stream.api.base-url:${delivery.api.base-url}}")
    private String baseUrl;

    public StreamLegacyRequestSpecFactory(
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            UsersProperties usersProperties
    ) {
        this.currentUserSessionProvider = currentUserSessionProvider;
        this.usersProperties = usersProperties;
    }

    public RequestSpecBuilder create() {
        User sender = currentUserSessionProvider.getObject().getUser();
        if (sender == null) {
            throw new IllegalStateException("Nessuna utenza in sessione per le chiamate pn-stream legacy");
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
                .addFilter(new HttpLoggingFilter())
                .addFilter(apiKeyHeader(apiKey));
    }

    private static Filter apiKeyHeader(String apiKey) {
        return (request, response, context) -> {
            request.header(API_KEY_HEADER, apiKey);
            return context.next(request, response);
        };
    }
}

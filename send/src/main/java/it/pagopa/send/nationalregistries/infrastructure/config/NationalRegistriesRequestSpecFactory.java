package it.pagopa.send.nationalregistries.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.infrastructure.config.UsersProperties;
import it.pagopa.send.generated.openapi.clients.nationalregistries.JacksonObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

/**
 * national-registries-private e' esposto dallo stesso gateway/tunnel di "delivery" (vedi
 * {@code national-registries.api.base-url} nei file di ambiente): sperimentalmente il gateway
 * risponde 403 senza x-api-key, quindi qui si riusa la stessa deliveryApiKey dell'utenza corrente
 * (vedi {@link it.pagopa.send.bff.delivery.infrastructure.config.DeliveryRequestSpecFactory}).
 */
@Component
public class NationalRegistriesRequestSpecFactory {

    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final UsersProperties usersProperties;

    @Value("${national-registries.api.base-url}")
    private String baseUrl;

    public NationalRegistriesRequestSpecFactory(
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            UsersProperties usersProperties
    ) {
        this.currentUserSessionProvider = currentUserSessionProvider;
        this.usersProperties = usersProperties;
    }

    public RequestSpecBuilder create() {
        User sender = currentUserSessionProvider.getObject().getUser();
        String apiKey = usersProperties.findByUsername(sender.getUsername())
                .map(UsersProperties.UserEntry::deliveryApiKey)
                .orElse(null);

        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setConfig(config().objectMapperConfig(
                        objectMapperConfig().defaultObjectMapper(JacksonObjectMapper.jackson())
                ))
                .addHeader("x-api-key", apiKey)
                .addFilter(new HttpLoggingFilter());
    }
}

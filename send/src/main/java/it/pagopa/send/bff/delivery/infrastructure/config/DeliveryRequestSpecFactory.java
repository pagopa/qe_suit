package it.pagopa.send.bff.delivery.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.infrastructure.config.UsersProperties;
import it.pagopa.send.generated.openapi.clients.delivery.JacksonObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeliveryRequestSpecFactory {

    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final UsersProperties usersProperties;

    @Value("${delivery.api.base-url}")
    private String basePath;

    public DeliveryRequestSpecFactory(
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
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Nessuna api-key delivery configurata per l'utenza " + sender.getUsername()
                            + " (campo delivery-api-key in users/application-users-<ambiente>.yaml)");
        }

        return new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(RestAssuredConfig.config()
                        .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                .defaultObjectMapper(JacksonObjectMapper.jackson())))
                .addHeader("x-api-key", apiKey)
                .addFilter(new HttpLoggingFilter());
    }
}

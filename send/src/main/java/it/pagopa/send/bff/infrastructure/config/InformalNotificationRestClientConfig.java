package it.pagopa.send.bff.infrastructure.config;

import it.pagopa.send.common.user.infrastructure.config.UsersProperties;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.informal.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.informal.api.NewInformalNotificationApi;
import it.pagopa.send.generated.openapi.clients.informal.api.SenderReadInformalNotificationB2BApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class InformalNotificationRestClientConfig {

    @Bean
    public NewInformalNotificationApi newInformalNotificationsApi(
            @Value("${delivery.informal.api.base-url}") String baseUrl,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            UsersProperties usersProperties
    ) {
        return NewInformalNotificationApi
                .newInformalNotification(
                        () -> {
                            String apiKey = resolveApiKey(currentUserSessionProvider, usersProperties);
                            return new RequestSpecBuilder()
                                    .setBaseUri(baseUrl)
                                    .setConfig(RestAssuredConfig.config()
                                            .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                                    .defaultObjectMapper(JacksonObjectMapper.jackson())))
                                    .addHeader("x-api-key", apiKey);
                        }
                );
    }

    @Bean
    public SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi(
            @Value("${delivery.informal.api.base-url}") String baseUrl,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            UsersProperties usersProperties
    ) {
        return SenderReadInformalNotificationB2BApi
                .senderReadInformalNotificationB2B(
                        () -> {
                            String apiKey = resolveApiKey(currentUserSessionProvider, usersProperties);
                            return new RequestSpecBuilder()
                                    .setBaseUri(baseUrl)
                                    .setConfig(RestAssuredConfig.config()
                                            .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                                    .defaultObjectMapper(JacksonObjectMapper.jackson())))
                                    .addHeader("x-api-key", apiKey);
                        }
                );
    }

    // api-key delivery della PA con cui si sta agendo (campo delivery-api-key in users/application-users-<ambiente>.yaml)
    private String resolveApiKey(ObjectProvider<CurrentUserSession> sessionProvider, UsersProperties usersProperties) {
        User sender = sessionProvider.getObject().getUser();
        return usersProperties.findByUsername(sender.getUsername())
                .map(UsersProperties.UserEntry::deliveryApiKey)
                .filter(key -> !key.isBlank())
                .orElseThrow(() -> new IllegalStateException(
                        "Nessuna api-key delivery configurata per l'utenza " + sender.getUsername()
                                + " (campo delivery-api-key in users/application-users-<ambiente>.yaml)"));
    }
}


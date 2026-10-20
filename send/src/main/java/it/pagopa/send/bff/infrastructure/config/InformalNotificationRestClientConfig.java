package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.informal.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.informal.api.NewInformalNotificationApi;
import it.pagopa.send.generated.openapi.clients.informal.api.SenderReadInformalNotificationB2BApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class InformalNotificationRestClientConfig {

    @Bean
    public NewInformalNotificationApi newInformalNotificationsApi(
            @Value("${delivery.informal.api.base-url}") String baseUrl,
            @Value("${pa.delivery-api-key.PALERMO:}") String defaultApiKey,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            Map<String, String> paDeliveryApiKeys
    ) {
        return NewInformalNotificationApi
                .newInformalNotification(
                        () -> {
                            String apiKey = resolveApiKey(currentUserSessionProvider, paDeliveryApiKeys, defaultApiKey);
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
            @Value("${pa.delivery-api-key.PALERMO:}") String defaultApiKey,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            Map<String, String> paDeliveryApiKeys
    ) {
        return SenderReadInformalNotificationB2BApi
                .senderReadInformalNotificationB2B(
                        () -> {
                            String apiKey = resolveApiKey(currentUserSessionProvider, paDeliveryApiKeys, defaultApiKey);
                            return new RequestSpecBuilder()
                                    .setBaseUri(baseUrl)
                                    .setConfig(RestAssuredConfig.config()
                                            .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                                    .defaultObjectMapper(JacksonObjectMapper.jackson())))
                                    .addHeader("x-api-key", apiKey);
                        }
                );
    }

    private String resolveApiKey(
            ObjectProvider<CurrentUserSession> sessionProvider,
            Map<String, String> paDeliveryApiKeys,
            String defaultApiKey
    ) {
        CurrentUserSession session = sessionProvider.getIfAvailable();
        if (session != null && session.getSender() != null && paDeliveryApiKeys != null) {
            Tenant sender = session.getSender();
            String apiKey = paDeliveryApiKeys.get(sender.name());
            if (apiKey != null && !apiKey.isBlank()) {
                return apiKey;
            }
        }
        return defaultApiKey;
    }
}


package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.api.SenderInformalNotificationsApi;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CampaignsRestClientConfig {

    @Bean
    public SenderInformalNotificationsApi senderInformalNotificationsApi(
            @Value("${bff.api.base-url}") String baseUrl,
            @Value("${bff.api.bearer-token:}") String fallbackBearerToken,
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            DynamicUserTokenProvider tokenProvider
    ) {
        return SenderInformalNotificationsApi
                .senderInformalNotifications(
                        () -> {
                            RequestSpecBuilder builder = new RequestSpecBuilder()
                                    .setBaseUri(baseUrl)
                                    .setConfig(io.restassured.config.RestAssuredConfig.config()
                                            .objectMapperConfig(io.restassured.config.ObjectMapperConfig.objectMapperConfig()
                                                    .defaultObjectMapper(it.pagopa.send.generated.openapi.clients.sender.informal.bff.JacksonObjectMapper.jackson())));
                            String token = resolveToken(currentUserSessionProvider, tokenProvider, fallbackBearerToken);
                            if (token != null && !token.isBlank()) {
                                builder.addHeader("Authorization", "Bearer " + token);
                            }
                            return builder;
                        }
                );
    }

    private String resolveToken(
            ObjectProvider<CurrentUserSession> sessionProvider,
            DynamicUserTokenProvider tokenProvider,
            String fallback
    ) {
        CurrentUserSession session = sessionProvider.getIfAvailable();
        if (session != null && session.hasUser()) {
            String token = tokenProvider.getToken(session.getUser().getUsername());
            if (token != null && !token.isBlank()) {
                return token;
            }
        }
        return fallback;
    }
}


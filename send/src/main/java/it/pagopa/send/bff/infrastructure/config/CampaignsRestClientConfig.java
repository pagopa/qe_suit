package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.generated.openapi.clients.bff.ApiClient;
import it.pagopa.send.generated.openapi.clients.bff.JacksonObjectMapper;
import it.pagopa.send.generated.openapi.clients.bff.api.NotificationSentApi;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.api.SenderInformalNotificationsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CampaignsRestClientConfig {
    @Bean
    public SenderInformalNotificationsApi senderInformalNotificationsApi(
            @Value("${bff.api.base-url}") String baseUrl,
            @Value("${bff.api.bearer-token}") String bearerToken
    ) {
        return SenderInformalNotificationsApi
                .senderInformalNotifications(
                        () -> new RequestSpecBuilder()
                                .setBaseUri(baseUrl)
                )
                .reqSpec(spec ->
                        spec.addHeader(
                                "Authorization",
                                "Bearer " + bearerToken
                        )
                );
    }
}

package it.pagopa.send.bff.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import it.pagopa.send.generated.openapi.clients.informal.api.NewInformalNotificationApi;
import it.pagopa.send.generated.openapi.clients.informal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.api.SenderInformalNotificationsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InformalNotificationRestClientConfig {
    @Bean
    public NewInformalNotificationApi newInformalNotificationsApi(
            @Value("${delivery.informal.api.base-url}") String baseUrl,
            @Value("${pa.delivery-api-key.PALERMO}") String xApiKey
    ) {
        return NewInformalNotificationApi
                .newInformalNotification(
                        () -> new RequestSpecBuilder()
                                .setBaseUri(baseUrl)
                )
                .reqSpec(spec ->
                        spec.addHeader(
                                "x-api-key",
                                xApiKey
                        )
                );
    }

    @Bean
    public SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi(
            @Value("${delivery.informal.api.base-url}") String baseUrl,
            @Value("${pa.delivery-api-key.PALERMO}") String xApiKey
    ) {
        return SenderReadInformalNotificationB2BApi
                .senderReadInformalNotificationB2B(
                        () -> new RequestSpecBuilder()
                                .setBaseUri(baseUrl)
                )
                .reqSpec(spec ->
                        spec.addHeader(
                                "x-api-key",
                                xApiKey
                        )
                );
    }
}

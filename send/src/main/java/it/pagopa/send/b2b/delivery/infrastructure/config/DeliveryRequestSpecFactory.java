package it.pagopa.send.b2b.delivery.infrastructure.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.generated.openapi.clients.delivery.JacksonObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeliveryRequestSpecFactory {

    private final ObjectProvider<CurrentUserSession> currentUserSessionProvider;
    private final Map<String, String> paDeliveryApiKeys;

    @Value("${delivery.api.base-url}")
    private String basePath;

    public DeliveryRequestSpecFactory(
            ObjectProvider<CurrentUserSession> currentUserSessionProvider,
            Map<String, String> paDeliveryApiKeys
    ) {
        this.currentUserSessionProvider = currentUserSessionProvider;
        this.paDeliveryApiKeys = paDeliveryApiKeys;
    }

    public RequestSpecBuilder create() {
        Tenant sender = currentUserSessionProvider.getObject().getSender();
        String apiKey = paDeliveryApiKeys.get(sender.name());
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Nessuna api-key configurata per il tenant " + sender.name()
                            + " (property pa.delivery-api-key." + sender.name() + ")");
        }

        return new RequestSpecBuilder()
                .setBaseUri(basePath)
                .setConfig(RestAssuredConfig.config()
                        .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                .defaultObjectMapper(JacksonObjectMapper.jackson())))
                .addHeader("x-api-key", apiKey);
    }
}

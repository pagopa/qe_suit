package it.pagopa.send.b2b.delivery.infrastructure.config;

import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.delivery.ApiClient;
import it.pagopa.send.generated.openapi.clients.delivery.api.SenderReadB2BApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeliveryClientConfig {

    @Bean
    public SenderReadB2BApi senderReadB2BApi(DeliveryRequestSpecFactory requestSpecFactory) {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create);

        return ApiClient.api(config).senderReadB2B();
    }

    /**
     * L'api-key per le API B2B delivery è unica per PA e per ambiente: va risolta per
     * {@link Tenant} al momento della chiamata (vedi {@link DeliveryRequestSpecFactory}), non può
     * essere un singolo valore iniettato nel bean del client. Valorizzata da
     * {@code pa.delivery-api-key} negli application-{profilo}.yaml (chiave = nome della costante
     * enum Tenant).
     */
    @Bean
    @ConfigurationProperties(prefix = "pa.delivery-api-key")
    public Map<String, String> paDeliveryApiKeys() {
        return new HashMap<>();
    }
}

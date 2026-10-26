package it.pagopa.send.bff.delivery.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.delivery.ApiClient;
import it.pagopa.send.generated.openapi.clients.delivery.api.SenderReadB2BApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryClientConfig {

    @Bean
    public SenderReadB2BApi senderReadB2BApi(DeliveryRequestSpecFactory requestSpecFactory) {
        ApiClient.Config config = ApiClient.Config.apiConfig()
                .reqSpecSupplier(requestSpecFactory::create);

        return ApiClient.api(config).senderReadB2B();
    }
}

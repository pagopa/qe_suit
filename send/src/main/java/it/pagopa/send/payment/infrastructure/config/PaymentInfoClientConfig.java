package it.pagopa.send.payment.infrastructure.config;

import it.pagopa.send.generated.openapi.clients.externalregistries.payment.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentInfoClientConfig {

    /**
     * Il {@code reqSpecSupplier} e' invocato alla costruzione di ogni operazione: il token dell'utenza
     * corrente viene letto in quel momento, dopo che il validator ha applicato l'identita' dichiarata in
     * {@code as(...)}.
     */
    @Bean("paymentInfoApiClient")
    public ApiClient paymentInfoApiClient(PaymentInfoRequestSpecFactory requestSpecFactory) {
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(requestSpecFactory::create));
    }
}

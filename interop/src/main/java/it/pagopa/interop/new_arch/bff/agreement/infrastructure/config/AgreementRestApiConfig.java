package it.pagopa.interop.new_arch.bff.agreement.infrastructure.config;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AgreementsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgreementRestApiConfig {
    @Bean
    public AgreementsApi agreementsApi(ApiClient apiClient) {
        return new AgreementsApi(apiClient);
    }
}

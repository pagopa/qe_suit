package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.application.context.EntityStore;
import it.pagopa.kernel.security.DPoPProofGateway;
import it.pagopa.kernel.security.DPoPProofService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonComponentConfig {

    @Bean
    DPoPProofService dPoPProofService() {
        return new DPoPProofService();
    }

    @Bean
    DPoPProofGateway dPoPProofGateway(DPoPProofService dPoPProofService, EntityStore entityStore) {
        return new DPoPProofGateway(dPoPProofService, entityStore);
    }
}

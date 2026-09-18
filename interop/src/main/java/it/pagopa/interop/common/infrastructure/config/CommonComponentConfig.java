package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.kernel.security.DPoPProofService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonComponentConfig {

    @Bean
    DPoPProofService dPoPProofService() {
        return new DPoPProofService();
    }
}

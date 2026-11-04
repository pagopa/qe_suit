package it.pagopa.send.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.send.common.infrastructure.contract.SendFuzzingProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    ObjectMapper objectMapper(SendFuzzingProfile fuzzingProfile) {
        return fuzzingProfile.objectMapper();
    }
}

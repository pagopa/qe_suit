package it.pagopa.interop.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.infrastructure.contract.InteropFuzzingProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    ObjectMapper objectMapper(InteropFuzzingProfile fuzzingProfile) {
        // Runtime and PST must share the same mapper configuration (object graph + mutation validity).
        return fuzzingProfile.objectMapper();
    }
}
package it.pagopa.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ObjectGraphConfiguration {

    @Bean
    ObjectGraphDecomposer objectGraphDecomposer(ObjectMapper objectMapper) {
        ObjectDecomposer decomposer = new JacksonObjectDecomposer(objectMapper);
        return new DefaultObjectGraphDecomposer(decomposer);
    }
}

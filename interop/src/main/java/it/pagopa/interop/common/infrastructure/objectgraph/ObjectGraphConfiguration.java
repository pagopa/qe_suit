package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ObjectGraphConfiguration {

    @Bean
    ObjectGraphFacade objectGraphFacade(ObjectMapper objectMapper) {
        ObjectDecomposer decomposer = new JacksonObjectDecomposer(objectMapper);
        return new DefaultObjectGraphFacade(decomposer);
    }
}

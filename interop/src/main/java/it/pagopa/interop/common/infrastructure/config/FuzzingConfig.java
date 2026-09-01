package it.pagopa.interop.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.*;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class FuzzingConfig {

    @Bean
    ObjectDecomposer objectDecomposer(ObjectMapper objectMapper) {
        return new JacksonObjectDecomposer(objectMapper);
    }

    @Bean
    ObjectGraphDecomposer objectGraphDecomposer(ObjectDecomposer objectDecomposer) {
        return new DefaultObjectGraphDecomposer(objectDecomposer);
    }

    @Bean
    FuzzMutationApplier fuzzMutationApplier(ObjectMapper objectMapper) {
        return new JacksonFuzzMutationApplier(objectMapper);
    }

    @Bean
    FuzzEngine fuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            List<FuzzRule> rules
    ) {
        return new DefaultFuzzEngine(
                objectGraphDecomposer,
                objectMapper,
                mutationApplier,
                rules
        );
    }

    @Bean
    FuzzRule nullAndMissingRule() {
        return new NullAndMissingRule();
    }

    @Bean
    FuzzRule scalarRule() {
        return new ScalarRule();
    }
}

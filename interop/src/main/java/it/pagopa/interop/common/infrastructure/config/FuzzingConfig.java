package it.pagopa.interop.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.*;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Bean("payloadFuzzEngine")
    FuzzEngine payloadFuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            NullAndMissingRule nullAndMissingRule,
            ScalarRule scalarRule
    ) {
        return new DefaultFuzzEngine(
                objectGraphDecomposer,
                objectMapper,
                mutationApplier,
                List.of(nullAndMissingRule, scalarRule)
        );
    }

    @Bean("pathParamsFuzzEngine")
    FuzzEngine pathParamsFuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            @Qualifier("scalarRule") ScalarRule scalarRule
    ) {
        return new DefaultFuzzEngine(
                objectGraphDecomposer,
                objectMapper,
                mutationApplier,
                List.of(scalarRule)
        );
    }

    @Bean
    NullAndMissingRule nullAndMissingRule() {
        return new NullAndMissingRule();
    }

    @Bean
    ScalarRule scalarRule() {
        return new ScalarRule();
    }
}

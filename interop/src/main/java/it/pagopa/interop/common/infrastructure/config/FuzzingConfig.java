package it.pagopa.interop.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.*;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.common.infrastructure.contract.InteropFuzzingProfile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            @Qualifier("payloadFuzzCasePlanner") FuzzCasePlanner fuzzCasePlanner
    ) {
        return new DefaultFuzzEngine(
                objectGraphDecomposer,
                objectMapper,
                mutationApplier,
                fuzzCasePlanner
        );
    }

    @Bean("pathParamsFuzzEngine")
    FuzzEngine pathParamsFuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            @Qualifier("pathParamsFuzzCasePlanner") FuzzCasePlanner fuzzCasePlanner
    ) {
        return new DefaultFuzzEngine(
                objectGraphDecomposer,
                objectMapper,
                mutationApplier,
                fuzzCasePlanner
        );
    }

    @Bean
    InteropFuzzingProfile interopFuzzingProfile() {
        return new InteropFuzzingProfile();
    }

    @Bean("payloadFuzzCasePlanner")
    FuzzCasePlanner payloadFuzzCasePlanner(InteropFuzzingProfile fuzzingProfile) {
        return fuzzingProfile.payloadPlanner();
    }

    @Bean("pathParamsFuzzCasePlanner")
    FuzzCasePlanner pathParamsFuzzCasePlanner(InteropFuzzingProfile fuzzingProfile) {
        return fuzzingProfile.pathParamsPlanner();
    }

    // Kept for backward compatibility: same instances used by the profile planners.
    @Bean
    NullAndMissingRule nullAndMissingRule(InteropFuzzingProfile fuzzingProfile) {
        return fuzzingProfile.nullAndMissingRule();
    }

    @Bean
    ScalarRule scalarRule(InteropFuzzingProfile fuzzingProfile) {
        return fuzzingProfile.scalarRule();
    }
}

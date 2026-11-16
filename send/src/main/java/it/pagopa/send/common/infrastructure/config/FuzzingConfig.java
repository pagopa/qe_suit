package it.pagopa.send.common.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.*;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.send.common.infrastructure.contract.SendFuzzingProfile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class FuzzingConfig {

    @Bean ObjectDecomposer objectDecomposer(ObjectMapper objectMapper) { return new JacksonObjectDecomposer(objectMapper); }
    @Bean ObjectGraphDecomposer objectGraphDecomposer(ObjectDecomposer objectDecomposer) { return new DefaultObjectGraphDecomposer(objectDecomposer); }
    @Bean FuzzMutationApplier fuzzMutationApplier(ObjectMapper objectMapper) { return new JacksonFuzzMutationApplier(objectMapper); }
    @Bean
    SendFuzzingProfile sendFuzzingProfile() { return new SendFuzzingProfile(); }

    @Bean("payloadFuzzCasePlanner") FuzzCasePlanner payloadFuzzCasePlanner(SendFuzzingProfile p) { return p.payloadPlanner(); }
    @Bean("pathParamsFuzzCasePlanner") FuzzCasePlanner pathParamsFuzzCasePlanner(SendFuzzingProfile p) { return p.pathParamsPlanner(); }
    @Bean("queryParamsFuzzCasePlanner") FuzzCasePlanner queryParamsFuzzCasePlanner(SendFuzzingProfile p) { return p.queryParamsPlanner(); }

    @Bean("payloadFuzzEngine")
    FuzzEngine payloadFuzzEngine(ObjectGraphDecomposer d, ObjectMapper m, FuzzMutationApplier a, @Qualifier("payloadFuzzCasePlanner") FuzzCasePlanner p) {
        return new DefaultFuzzEngine(d, m, a, p);
    }

    @Bean("pathParamsFuzzEngine")
    FuzzEngine pathParamsFuzzEngine(ObjectGraphDecomposer d, ObjectMapper m, FuzzMutationApplier a, @Qualifier("pathParamsFuzzCasePlanner") FuzzCasePlanner p) {
        return new DefaultFuzzEngine(d, m, a, p);
    }

    @Bean("queryParamsFuzzEngine")
    FuzzEngine queryParamsFuzzEngine(ObjectGraphDecomposer d, ObjectMapper m, FuzzMutationApplier a, @Qualifier("queryParamsFuzzCasePlanner") FuzzCasePlanner p) {
        return new DefaultFuzzEngine(d, m, a, p);
    }
}

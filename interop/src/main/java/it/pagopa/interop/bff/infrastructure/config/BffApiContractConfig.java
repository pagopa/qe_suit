package it.pagopa.interop.bff.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.interop.common.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class BffApiContractConfig {

    @Bean("bffApiContract")
    HttpContractValidator bffApiContract(
            ObjectMapper objectMapper,
            FuzzEngine fuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            HttpContractPolicy bffApiContractPolicy
    ) {
        return new HttpContractValidator(
                objectMapper,
                fuzzEngine,
                objectGraphDecomposer,
                bffApiContractPolicy
        );
    }

    @Bean
    HttpContractPolicy bffApiContractPolicy() {
        return HttpContractPolicy.builder()
                .successStatus(200)
                .scenarioStatus(List.of(
                        FuzzScenario.REPLACED_WITH_NULL,
                        FuzzScenario.REMOVED,
                        FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        FuzzScenario.REPLACED_WITH_BLANK_STRING,
                        FuzzScenario.REPLACED_WITH_LONG_STRING,
                        FuzzScenario.REPLACED_WITH_SQL_INJECTION,
                        FuzzScenario.REPLACED_WITH_WRONG_TYPE_STRING,
                        FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER,
                        FuzzScenario.REPLACED_WITH_WRONG_TYPE_DECIMAL,
                        FuzzScenario.REPLACED_WITH_ZERO,
                        FuzzScenario.REPLACED_WITH_NEGATIVE_VALUE,
                        FuzzScenario.REPLACED_WITH_MIN_VALUE,
                        FuzzScenario.REPLACED_WITH_MAX_VALUE,
                        FuzzScenario.REPLACED_WITH_MALFORMED_UUID,
                        FuzzScenario.REPLACED_WITH_NIL_UUID,
                        FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM
                ), 400)
                .scenarioStatus(FuzzScenario.REPLACED_WITH_XSS, 403)
                .build();
    }
}
package it.pagopa.send.bff.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class TemplateApiContractConfig {

    @Bean("templateApiContract")
    HttpContractValidator templateApiContract(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            HttpContractPolicy templateApiContractPolicy
    ) {
        return new HttpContractValidator(
                objectMapper,
                payloadFuzzEngine,
                pathParamsFuzzEngine,
                objectGraphDecomposer,
                templateApiContractPolicy
        );
    }

    @Bean
    SendHttpContractValidator sendHttpContractValidator(
            HttpContractValidator templateApiContract,
            CurrentUserSession currentUserSession
    ) {
        return new SendHttpContractValidator(templateApiContract, currentUserSession);
    }

    @Bean
    HttpContractPolicy templateApiContractPolicy() {
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
                        FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM,
                        FuzzScenario.REPLACED_WITH_AMPERSAND,
                        FuzzScenario.REPLACED_WITH_LESS_THAN,
                        FuzzScenario.REPLACED_WITH_GREATER_THAN,
                        FuzzScenario.REPLACED_WITH_DOUBLE_QUOTE,
                        FuzzScenario.REPLACED_WITH_SINGLE_QUOTE
                ), 200)
                .scenarioStatus(FuzzScenario.REPLACED_WITH_XSS, 403)
                .build();
    }
}
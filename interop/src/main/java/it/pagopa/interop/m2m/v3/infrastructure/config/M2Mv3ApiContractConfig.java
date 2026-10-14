package it.pagopa.interop.m2m.v3.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class M2Mv3ApiContractConfig {

    @Bean("m2mV3ApiContractValidator")
    HttpContractValidator m2mV3ApiContractValidator(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("m2mV3ApiContractPolicy") HttpContractPolicy m2mV3ApiContractPolicy
    ) {
        return new HttpContractValidator(
                objectMapper,
                payloadFuzzEngine,
                pathParamsFuzzEngine,
                queryParamsFuzzEngine,
                objectGraphDecomposer,
                m2mV3ApiContractPolicy
        );
    }

    @Bean("m2mV3InteropHttpContractValidator")
    InteropHttpContractValidator interopHttpContractValidator(
            @Qualifier("m2mV3ApiContractValidator") HttpContractValidator m2mV3ApiContractValidator,
            CurrentUserSession currentUserSession,
            CurrentM2MSession currentM2MSession
    ) {
        return new InteropHttpContractValidator(m2mV3ApiContractValidator, currentUserSession, currentM2MSession);
    }

    @Bean(name = "m2mV3ApiContractPolicy")
    HttpContractPolicy m2mV3ApiContractPolicy() {
        return HttpContractPolicy.builder()
                .successStatus(201)
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
                        FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM
                ), 400)
                .scenarioStatus(FuzzScenario.REPLACED_WITH_NIL_UUID, 404)
                .scenarioStatus(FuzzScenario.REPLACED_WITH_XSS, 403)
                .build();
    }
}

package it.pagopa.interop.bff.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.*;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.common.infrastructure.contract.InteropHttpContractValidator;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class BffApiContractConfig {
    public static final int DEFAULT_SUCCESS_STATUS_CODE = 200;

    @Bean("bffApiContract")
    HttpContractValidator bffApiContract(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("bffApiContractPolicy") HttpContractPolicy bffApiContractPolicy
    ) {
        return new HttpContractValidator(
                objectMapper,
                payloadFuzzEngine,
                pathParamsFuzzEngine,
                queryParamsFuzzEngine,
                objectGraphDecomposer,
                bffApiContractPolicy
        );
    }

    /* FIXME 29/09/2026: per risolvere presto il problema di conflitto tra bean è stato posto
    *   @Primary, ma il bean va restituito in funzione del test (bff o m2m). */
    @Primary

    @Bean("bffInteropHttpContractValidator")
    InteropHttpContractValidator interopHttpContractValidator(
            @Qualifier("bffApiContract") HttpContractValidator bffApiContract,
            CurrentUserSession currentUserSession,
            CurrentM2MSession currentM2MSession
    ) {
        return new InteropHttpContractValidator(bffApiContract, currentUserSession, currentM2MSession);
    }

    @Bean("bffApiContractPolicy")
    HttpContractPolicy bffApiContractPolicy() {
        return HttpContractPolicy.builder()
                .successStatus(DEFAULT_SUCCESS_STATUS_CODE)
                .scenarioStatus(List.of(
                        FuzzScenario.REPLACED_WITH_NULL,
                        FuzzScenario.REMOVED,
                        FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        FuzzScenario.REPLACED_WITH_BLANK_STRING,
                        FuzzScenario.REPLACED_WITH_LONG_STRING,
                        FuzzScenario.REPLACED_WITH_SQL_INJECTION,
                        FuzzScenario.REPLACED_WITH_URL_INJECTION,
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
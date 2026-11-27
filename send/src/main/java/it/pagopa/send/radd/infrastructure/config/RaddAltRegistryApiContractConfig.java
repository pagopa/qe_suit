package it.pagopa.send.radd.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

/**
 * Validator e policy dei contract test del registry RADD ({@code pn-radd-alt-registry-v2-external}). Il JWT e'
 * statico (vedi {@link RaddAltRegistryClientConfig}), quindi non c'e' un'utenza in sessione: si usa direttamente
 * {@link HttpContractValidator#apiCall(java.util.function.Supplier)}.
 * <p>
 * Aspettative derivate dallo spec, da confermare al primo run: scenari di validazione 400 (incluso il {@code null}
 * esplicito, che lo spec dichiara rifiutato dal gateway), NIL uuid 404 (sede inesistente), XSS 403 (WAF del gateway).
 * {@code deleteRegistry} risponde 204 senza body, quindi ha una policy dedicata.
 */
@Configuration(proxyBeanMethods = false)
public class RaddAltRegistryApiContractConfig {

    public static final int SUCCESS_STATUS_CODE = 200;
    public static final int NO_CONTENT_STATUS_CODE = 204;
    public static final int BAD_REQUEST_STATUS_CODE = 400;

    private static final List<FuzzScenario> VALIDATION_ERROR_SCENARIOS = List.of(
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
    );

    @Bean("raddRegistryApiContractPolicy")
    HttpContractPolicy raddRegistryApiContractPolicy() {
        return policy(status(SUCCESS_STATUS_CODE));
    }

    @Bean("raddRegistryDeleteApiContractPolicy")
    HttpContractPolicy raddRegistryDeleteApiContractPolicy() {
        return policy(status(NO_CONTENT_STATUS_CODE));
    }

    @Bean("raddRegistryHttpContractValidator")
    HttpContractValidator raddRegistryHttpContractValidator(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("raddRegistryApiContractPolicy") HttpContractPolicy policy
    ) {
        return new HttpContractValidator(
                objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
    }

    @Bean("raddRegistryDeleteHttpContractValidator")
    HttpContractValidator raddRegistryDeleteHttpContractValidator(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("raddRegistryDeleteApiContractPolicy") HttpContractPolicy policy
    ) {
        return new HttpContractValidator(
                objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
    }

    public static Consumer<Response> status(int status) {
        return response -> response.then().statusCode(status);
    }

    private static HttpContractPolicy policy(Consumer<Response> success) {
        return HttpContractPolicy.builder()
                .success(success)
                .scenario(VALIDATION_ERROR_SCENARIOS, status(BAD_REQUEST_STATUS_CODE))
                .scenario(FuzzScenario.REPLACED_WITH_NIL_UUID, status(404))
                .scenario(FuzzScenario.REPLACED_WITH_XSS, status(403))
                .build();
    }
}

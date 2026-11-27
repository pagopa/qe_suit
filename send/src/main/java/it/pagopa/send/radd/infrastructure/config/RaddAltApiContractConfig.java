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

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Validator e policy dei contract test RADD. Il token e' statico (vedi {@link RaddRequestSpecFactory}), quindi non
 * c'e' un'utenza in sessione: si usa direttamente {@link HttpContractValidator#apiCall(java.util.function.Supplier)}.
 * <p>
 * Aspettative derivate dallo spec, da confermare al primo run: scenari di validazione 400, NIL uuid 404 (risorsa
 * inesistente), XSS 403 (WAF del gateway). RADD restituisce molti esiti di business come HTTP 200 con
 * {@code status.code} (es. 10 "input non valido"): se al primo run e' cosi', si sostituisce {@code status(400)} con
 * {@link #businessError()}.
 */
@Configuration(proxyBeanMethods = false)
public class RaddAltApiContractConfig {

    public static final int SUCCESS_STATUS_CODE = 200;

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

    @Bean("raddApiContractPolicy")
    HttpContractPolicy raddApiContractPolicy() {
        return policy(status(SUCCESS_STATUS_CODE));
    }

    /**
     * {@code documentDownload} restituisce il PDF del frontespizio.
     */
    @Bean("raddDownloadApiContractPolicy")
    HttpContractPolicy raddDownloadApiContractPolicy() {
        return policy(response -> response.then()
                .statusCode(SUCCESS_STATUS_CODE)
                .contentType(startsWith("application/pdf")));
    }

    @Bean("raddHttpContractValidator")
    HttpContractValidator raddHttpContractValidator(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("raddApiContractPolicy") HttpContractPolicy policy
    ) {
        return new HttpContractValidator(
                objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
    }

    @Bean("raddDownloadHttpContractValidator")
    HttpContractValidator raddDownloadHttpContractValidator(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("raddDownloadApiContractPolicy") HttpContractPolicy policy
    ) {
        return new HttpContractValidator(
                objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
    }

    /**
     * Esito KO di business: HTTP 200 con {@code status.code} diverso da 0.
     */
    public static Consumer<Response> businessError() {
        return response -> response.then()
                .statusCode(SUCCESS_STATUS_CODE)
                .body("status.code", not(0));
    }

    public static Consumer<Response> status(int status) {
        return response -> response.then().statusCode(status);
    }

    private static HttpContractPolicy policy(Consumer<Response> success) {
        return HttpContractPolicy.builder()
                .success(success)
                .scenario(VALIDATION_ERROR_SCENARIOS, status(400))
                .scenario(FuzzScenario.REPLACED_WITH_NIL_UUID, status(404))
                .scenario(FuzzScenario.REPLACED_WITH_XSS, status(403))
                .build();
    }
}

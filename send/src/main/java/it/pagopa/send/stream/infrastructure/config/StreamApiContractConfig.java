package it.pagopa.send.stream.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.stream.infrastructure.StreamCreatedTracker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Validator e policy dei contract test pn-stream v3.0, sul modello di {@code BffApiContractConfig}
 * di interop: stessi scenari a 400, SQL injection esclusa, NIL uuid 404 (risorsa inesistente), XSS
 * 403 (bloccata dal WAF del gateway). Lo status di successo dipende dall'operazione (lo spec dichiara
 * 204 per remove, 200 per tutte le altre), da qui le policy distinte, come in {@code M2Mv3ApiContractConfig}:
 * <ul>
 *   <li>{@code streamHttpContractValidator}: successo 200;</li>
 *   <li>{@code streamCreateHttpContractValidator}: successo 200, ogni risposta e' tracciata per
 *       rimuovere gli stream creati (limite di 10 stream attivi per PA);</li>
 *   <li>{@code streamRemoveHttpContractValidator}: successo 204.</li>
 * </ul>
 */
@Configuration(proxyBeanMethods = false)
public class StreamApiContractConfig {

    public static final int DEFAULT_SUCCESS_STATUS_CODE = 200;
    public static final int REMOVE_SUCCESS_STATUS_CODE = 204;

    private static final List<FuzzScenario> VALIDATION_ERROR_SCENARIOS = List.of(
            FuzzScenario.REPLACED_WITH_NULL,
            FuzzScenario.REMOVED,
            FuzzScenario.REPLACED_WITH_EMPTY_STRING,
            FuzzScenario.REPLACED_WITH_BLANK_STRING,
            FuzzScenario.REPLACED_WITH_LONG_STRING,
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

    @Bean("streamApiContractPolicy")
    HttpContractPolicy streamApiContractPolicy() {
        return policy(DEFAULT_SUCCESS_STATUS_CODE, UnaryOperator.identity());
    }

    @Bean("streamCreateApiContractPolicy")
    HttpContractPolicy streamCreateApiContractPolicy(StreamCreatedTracker createdTracker) {
        return policy(DEFAULT_SUCCESS_STATUS_CODE, createdTracker::tracking);
    }

    @Bean("streamRemoveApiContractPolicy")
    HttpContractPolicy streamRemoveApiContractPolicy() {
        return policy(REMOVE_SUCCESS_STATUS_CODE, UnaryOperator.identity());
    }

    @Bean("streamHttpContractValidator")
    SendHttpContractValidator streamHttpContractValidator(
            Engines engines, @Qualifier("streamApiContractPolicy") HttpContractPolicy policy,
            CurrentUserSession currentUserSession, UserProvider userProvider) {
        return new SendHttpContractValidator(engines.validator(policy), currentUserSession, userProvider);
    }

    @Bean("streamCreateHttpContractValidator")
    SendHttpContractValidator streamCreateHttpContractValidator(
            Engines engines, @Qualifier("streamCreateApiContractPolicy") HttpContractPolicy policy,
            CurrentUserSession currentUserSession, UserProvider userProvider) {
        return new SendHttpContractValidator(engines.validator(policy), currentUserSession, userProvider);
    }

    @Bean("streamRemoveHttpContractValidator")
    SendHttpContractValidator streamRemoveHttpContractValidator(
            Engines engines, @Qualifier("streamRemoveApiContractPolicy") HttpContractPolicy policy,
            CurrentUserSession currentUserSession, UserProvider userProvider) {
        return new SendHttpContractValidator(engines.validator(policy), currentUserSession, userProvider);
    }

    @Bean
    Engines streamContractEngines(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer
    ) {
        return new Engines(objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer);
    }

    /**
     * Policy di dominio; {@code decorator} avvolge ogni aspettativa (es. tracking degli stream creati).
     */
    private static HttpContractPolicy policy(int successStatus, UnaryOperator<Consumer<Response>> decorator) {
        return HttpContractPolicy.builder()
                .success(decorator.apply(status(successStatus)))
                .scenario(VALIDATION_ERROR_SCENARIOS, decorator.apply(status(400)))
                .skipScenario(FuzzScenario.REPLACED_WITH_SQL_INJECTION)
                .scenario(FuzzScenario.REPLACED_WITH_NIL_UUID, decorator.apply(status(404)))
                .scenario(FuzzScenario.REPLACED_WITH_XSS, decorator.apply(status(403)))
                .build();
    }

    private static Consumer<Response> status(int status) {
        return response -> response.then().statusCode(status);
    }

    /**
     * Dipendenze comuni ai tre {@link HttpContractValidator}, che differiscono solo per la policy.
     */
    record Engines(
            ObjectMapper objectMapper,
            FuzzEngine payloadFuzzEngine,
            FuzzEngine pathParamsFuzzEngine,
            FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer
    ) {
        HttpContractValidator validator(HttpContractPolicy policy) {
            return new HttpContractValidator(
                    objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
        }
    }
}

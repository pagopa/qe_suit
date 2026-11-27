package it.pagopa.send.payment.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.HttpContractPolicy;
import it.pagopa.infrastructure.contract.http.HttpContractValidator;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.UserProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Validator e policy dei contract test di {@code pn-payment-info-external-v1.yaml} (pn-external-registries).
 * <p>
 * Ogni mutazione standard e' attesa come errore di validazione 400, come nelle altre suite di fuzzing. Le chiamate
 * passano dall'ALB interno, senza WAF: anche XSS arriva al microservizio, che deve rifiutarlo con 400. Dove lo spec
 * non pone vincoli (testo libero, {@code returnUrl}, importo massimo) l'ammissione dell'input non si deduce: resta
 * alla policy finche' il team non la definisce.
 * <p>
 * Lo spec dichiara 200 come successo per tutte e tre le operazioni.
 */
@Configuration(proxyBeanMethods = false)
public class PaymentInfoApiContractConfig {

    public static final int SUCCESS_STATUS_CODE = 200;
    private static final int VALIDATION_ERROR_STATUS_CODE = 400;

    private static final List<FuzzScenario> VALIDATION_ERROR_SCENARIOS = List.of(
            FuzzScenario.REPLACED_WITH_NULL,
            FuzzScenario.REMOVED,
            FuzzScenario.REPLACED_WITH_EMPTY_STRING,
            FuzzScenario.REPLACED_WITH_BLANK_STRING,
            FuzzScenario.REPLACED_WITH_LONG_STRING,
            FuzzScenario.REPLACED_WITH_SQL_INJECTION,
            FuzzScenario.REPLACED_WITH_URL_INJECTION,
            FuzzScenario.REPLACED_WITH_XSS,
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
    );

    @Bean("paymentInfoApiContractPolicy")
    HttpContractPolicy paymentInfoApiContractPolicy() {
        return HttpContractPolicy.builder()
                .successStatus(SUCCESS_STATUS_CODE)
                .scenarioStatus(VALIDATION_ERROR_SCENARIOS, VALIDATION_ERROR_STATUS_CODE)
                .build();
    }

    @Bean("paymentInfoHttpContractValidator")
    SendHttpContractValidator paymentInfoHttpContractValidator(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            @Qualifier("paymentInfoApiContractPolicy") HttpContractPolicy policy,
            CurrentUserSession currentUserSession,
            UserProvider userProvider
    ) {
        HttpContractValidator delegate = new HttpContractValidator(
                objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
        return new SendHttpContractValidator(delegate, currentUserSession, userProvider);
    }
}

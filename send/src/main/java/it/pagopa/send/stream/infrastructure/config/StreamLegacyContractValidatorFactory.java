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
import it.pagopa.send.common.stream.domain.StreamApiVersion;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.stream.infrastructure.StreamLegacyCreatedTracker;
import it.pagopa.send.stream.infrastructure.StreamLegacyStatusCodes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Validator e policy dei contract test pn-stream legacy, sul modello della config dei contract
 * test pn-stream v3.0 (QA-19146). Le policy sono due, perche' le regole di status cambiano solo tra v1.0 e v2.x:
 * <ul>
 *   <li>v2.x: scenari di validazione 400, NIL uuid 404 (stream inesistente), XSS 403 (WAF);</li>
 *   <li>v1.0: come v2.x, ma la lambda {@code pn-versioningV1V2StreamLambda} converte in 400 i 403
 *       e 404 del backend, che lo spec v1.0 non dichiara: NIL uuid 400. L'XSS e' bloccato dal WAF
 *       prima della lambda, quindi resta 403.</li>
 * </ul>
 * Gli status sono quelli di {@link StreamLegacyStatusCodes}. Lo status di successo dipende
 * dall'operazione (204 per remove, 200 per le altre); il validator di create traccia ogni stream
 * creato, per rimuoverlo con la sua versione.
 */
@Component
public class StreamLegacyContractValidatorFactory {

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

    private final ObjectMapper objectMapper;
    private final FuzzEngine payloadFuzzEngine;
    private final FuzzEngine pathParamsFuzzEngine;
    private final FuzzEngine queryParamsFuzzEngine;
    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final CurrentUserSession currentUserSession;
    private final UserProvider userProvider;
    private final StreamLegacyCreatedTracker createdTracker;

    public StreamLegacyContractValidatorFactory(
            ObjectMapper objectMapper,
            @Qualifier("payloadFuzzEngine") FuzzEngine payloadFuzzEngine,
            @Qualifier("pathParamsFuzzEngine") FuzzEngine pathParamsFuzzEngine,
            @Qualifier("queryParamsFuzzEngine") FuzzEngine queryParamsFuzzEngine,
            ObjectGraphDecomposer objectGraphDecomposer,
            CurrentUserSession currentUserSession,
            UserProvider userProvider,
            StreamLegacyCreatedTracker createdTracker
    ) {
        this.objectMapper = objectMapper;
        this.payloadFuzzEngine = payloadFuzzEngine;
        this.pathParamsFuzzEngine = pathParamsFuzzEngine;
        this.queryParamsFuzzEngine = queryParamsFuzzEngine;
        this.objectGraphDecomposer = objectGraphDecomposer;
        this.currentUserSession = currentUserSession;
        this.userProvider = userProvider;
        this.createdTracker = createdTracker;
    }

    /**
     * Operazioni con successo 200 (list, retrieve, update, disable, consume).
     */
    public SendHttpContractValidator standard(StreamApiVersion version) {
        return validator(policy(version, StreamLegacyStatusCodes.SUCCESS, UnaryOperator.identity()));
    }

    /**
     * Create (successo 200): ogni risposta e' tracciata per rimuovere gli stream creati.
     */
    public SendHttpContractValidator create(StreamApiVersion version) {
        return validator(policy(version, StreamLegacyStatusCodes.SUCCESS, expectation -> createdTracker.tracking(version, expectation)));
    }

    /**
     * Remove (successo 204).
     */
    public SendHttpContractValidator remove(StreamApiVersion version) {
        return validator(policy(version, StreamLegacyStatusCodes.REMOVE_SUCCESS, UnaryOperator.identity()));
    }

    private SendHttpContractValidator validator(HttpContractPolicy policy) {
        HttpContractValidator delegate = new HttpContractValidator(
                objectMapper, payloadFuzzEngine, pathParamsFuzzEngine, queryParamsFuzzEngine, objectGraphDecomposer, policy);
        return new SendHttpContractValidator(delegate, currentUserSession, userProvider);
    }

    private static HttpContractPolicy policy(
            StreamApiVersion version, int successStatus, UnaryOperator<Consumer<Response>> decorator) {
        return HttpContractPolicy.builder()
                .success(decorator.apply(status(successStatus)))
                .scenario(VALIDATION_ERROR_SCENARIOS, decorator.apply(status(StreamLegacyStatusCodes.VALIDATION_ERROR)))
                .scenario(FuzzScenario.REPLACED_WITH_NIL_UUID, decorator.apply(status(StreamLegacyStatusCodes.notFound(version))))
                .scenario(FuzzScenario.REPLACED_WITH_XSS, decorator.apply(status(StreamLegacyStatusCodes.WAF_BLOCKED)))
                .build();
    }

    private static Consumer<Response> status(int status) {
        return response -> response.then().statusCode(status);
    }
}

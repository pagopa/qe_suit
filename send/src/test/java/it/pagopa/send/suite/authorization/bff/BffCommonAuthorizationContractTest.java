package it.pagopa.send.suite.authorization.bff;

import it.pagopa.send.TestBootApp;
import it.pagopa.send.bff.infrastructure.config.BffApiContractConfig;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.generated.openapi.clients.bff.ApiClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Endpoint <strong>universali</strong>: aperti a PA, PF e PG senza distinzione di platea
 * ({@code x-pn-allowed-cx-types} dello spec li dichiara validi per tutti e tre). Non esiste
 * quindi un'utenza da respingere: a differenza delle altre classi di questo package, qui non
 * c'e' un gruppo "Forbidden". La controprova verifica l'esatto opposto, cioe' che
 * <strong>nessuno</strong> dei tre tipi riceva un {@code 403} che il contratto non prevede.
 * <p>
 * Le chiamate compaiono nei test come {@code api#1}, {@code api#2}, ... nell'ordine in cui sono
 * elencate qui; metodo e URI effettivi sono nei log e nel messaggio di fallimento.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class},
        // `include` e non `spring.profiles.active`: il profilo `junit` attiva JunitContextConfig
        // aggiungendosi a quello d'ambiente (`test`), che porta le property come
        // `delivery.api.base-url`. Sostituirlo lascerebbe il contesto senza quelle property.
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffCommonAuthorizationContractTest {

    /** Una utenza per ciascuna platea: PA, PF e PG. */
    private static final List<UserAlias> EVERY_USER_TYPE =
            List.of(UserAlias.COMUNE_2, UserAlias.USER_2, UserAlias.IMPRESA_2);

    private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /** {@code UserConsents} v2: 2 endpoint aperti a PA, PF e PG. */
    @TestFactory
    Stream<DynamicTest> tosPrivacyV2IsAllowedForEveryone() {
        return validator.as(EVERY_USER_TYPE).apiCalls(tosPrivacyV2ApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] tosPrivacyV2ApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.userConsents().getTosPrivacyV2(),
                () -> apiClient.userConsents().acceptTosPrivacyV2()
        };
    }

    /** {@code Downtime}: 3 endpoint aperti a PA, PF e PG. */
    @TestFactory
    Stream<DynamicTest> downtimeIsAllowedForEveryone() {
        return validator.as(EVERY_USER_TYPE).apiCalls(downtimeApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] downtimeApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.downtime().getCurrentStatusV1(),
                () -> apiClient.downtime().getStatusHistoryV1(),
                () -> apiClient.downtime().getLegalFactV1().legalFactIdPath(UNKNOWN_ID)
        };
    }
}

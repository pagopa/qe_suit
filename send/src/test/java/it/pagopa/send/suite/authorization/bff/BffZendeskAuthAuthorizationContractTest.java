package it.pagopa.send.suite.authorization.bff;

import it.pagopa.send.TestBootApp;
import it.pagopa.send.bff.infrastructure.config.ZendeskAuthClientConfig;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.generated.openapi.clients.zendeskauth.ApiClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Endpoint di {@code pn-zendesk-auth}: riservato alla sola platea PF, sia PA sia PG non devono
 * potervi accedere.
 * <p>
 * Le chiamate compaiono nei test come {@code api#1}, {@code api#2}, ... nell'ordine in cui sono
 * elencate qui; metodo e URI effettivi sono nei log e nel messaggio di fallimento.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, ZendeskAuthClientConfig.class},
        // `include` e non `spring.profiles.active`: il profilo `junit` attiva JunitContextConfig
        // aggiungendosi a quello d'ambiente (`test`), che porta le property come
        // `zendesk.api.base-url`. Sostituirlo lascerebbe il contesto senza quelle property.
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffZendeskAuthAuthorizationContractTest {

    /** Utenze fuori dalla platea PF. */
    private static final List<UserAlias> NON_PF_USERS = List.of(UserAlias.COMUNE_1, UserAlias.IMPRESA_2);

    /** Utenza della platea PF: la controprova verifica che non venga respinta. */
    private static final List<UserAlias> PF_USERS = List.of(UserAlias.USER_2);

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /** {@code SupportRequest}: 1 endpoint, riservato alla PF. */
    @TestFactory
    Stream<DynamicTest> newSupportRequestIsForbiddenForNonPf() {
        return validator.as(NON_PF_USERS).apiCalls(newSupportRequestApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> newSupportRequestIsAllowedForPf() {
        return validator.as(PF_USERS).apiCalls(newSupportRequestApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] newSupportRequestApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.supportRequest().newSupportRequest()
        };
    }
}

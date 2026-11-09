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
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.List;
import java.util.stream.Stream;

/**
 * Autorizzazioni del <strong>Partner Tecnologico</strong>: con la sessione del PT {@code PT_1} le API riservate
 * all'ente mittente e ai destinatari rispondono 403, mentre l'API degli enti deleganti è richiamabile solo dal PT.
 * <p>
 * Le chiamate compaiono nei test come {@code api#1}, {@code api#2} nell'ordine in cui sono elencate; metodo e URI
 * effettivi sono nei log e nel messaggio di fallimento.
 * <p>
 * Gira solo negli ambienti in cui il BackOffice PT è disponibile ({@code partner-tecnologico.enabled}).
 */
@EnabledIf(expression = "${partner-tecnologico.enabled}", loadContext = true)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class BffPtAuthorizationContractTest {

    private static final int OK = 200;
    private static final int FORBIDDEN = 403;

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /**
     * API negate al PT, 403 atteso:
     * <ul>
     *     <li>api#1: elenco delle notifiche inviate, del portale ente mittente;</li>
     *     <li>api#2: elenco delle notifiche ricevute, del portale imprese.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> paAndPgApisAreForbiddenToPartner() {
        return validator.as(List.of(UserAlias.PT_1))
                .apiCalls(
                        () -> apiClient.notificationSent().searchSentNotificationsV1(), // API PA
                        () -> apiClient.notificationReceived().searchReceivedNotificationsV1() // API PG/PF
                )
                .expectedStatus(FORBIDDEN)
                .tests();
    }

    /**
     * API degli enti deleganti del PT, 403 atteso per PA, PG e PF.
     */
    @TestFactory
    Stream<DynamicTest> partnerApisAreForbiddenToOtherUsers() {
        return validator.as(List.of(UserAlias.COMUNE_1, UserAlias.IMPRESA_1, UserAlias.USER_1))
                .apiCalls(() -> apiClient.ptInstitutions().getPtInstitutionsV1())
                .expectedStatus(FORBIDDEN)
                .tests();
    }

    /**
     * API degli enti deleganti del PT, 200 atteso per il PT {@code PT_1}.
     */
    @TestFactory
    Stream<DynamicTest> partnerApisAreAllowedToPartner() {
        return validator.as(List.of(UserAlias.PT_1))
                .apiCalls(() -> apiClient.ptInstitutions().getPtInstitutionsV1())
                .expectedStatus(OK)
                .tests();
    }
}

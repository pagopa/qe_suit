package it.pagopa.send.suite.contract;

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
import java.util.stream.Stream;

/**
 * Endpoint della platea <strong>impresa PG</strong> (chiavi di integrazione e consensi PG): una PA
 * e una persona fisica non devono potervi accedere.
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
public class BffOwnerPgAuthorizationContractTest {

    /** Utenze fuori dalla platea PG: una PA e una PF. */
    private static final List<UserAlias> NON_PG_USERS = List.of(UserAlias.COMUNE_1, UserAlias.USER_1);

    private static final String UNKNOWN_KID = "00000000-0000-0000-0000-000000000000";

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /** {@code PublicKeys}: 6 endpoint. */
    @TestFactory
    Stream<DynamicTest> publicKeysAreForbiddenOutsidePg() {
        return validator
                .as(NON_PG_USERS)
                .apiCalls(
                        () -> apiClient.publicKeys().getPublicKeysV1(),
                        () -> apiClient.publicKeys().newPublicKeyV1(),
                        () -> apiClient.publicKeys().checkIssuerPublicKeyV1(),
                        () -> apiClient.publicKeys().deletePublicKeyV1().kidPath(UNKNOWN_KID),
                        () -> apiClient.publicKeys().rotatePublicKeyV1().kidPath(UNKNOWN_KID),
                        () -> apiClient.publicKeys().changeStatusPublicKeyV1().kidPath(UNKNOWN_KID)
                )
                .tests();
    }

    /** {@code VirtualKeys}: 4 endpoint. */
    @TestFactory
    Stream<DynamicTest> virtualKeysAreForbiddenOutsidePg() {
        return validator
                .as(NON_PG_USERS)
                .apiCalls(
                        () -> apiClient.virtualKeys().getVirtualKeysV1(),
                        () -> apiClient.virtualKeys().newVirtualKeyV1(),
                        () -> apiClient.virtualKeys().deleteVirtualKeyV1().kidPath(UNKNOWN_KID),
                        () -> apiClient.virtualKeys().changeStatusVirtualKeysV1().kidPath(UNKNOWN_KID)
                )
                .tests();
    }

    /** Consensi e gruppi specifici PG: 3 endpoint. */
    @TestFactory
    Stream<DynamicTest> pgConsentsAndGroupsAreForbiddenOutsidePg() {
        return validator
                .as(NON_PG_USERS)
                .apiCalls(
                        () -> apiClient.userConsents().getPgTosPrivacyV1(),
                        () -> apiClient.userConsents().acceptPgTosPrivacyV1(),
                        () -> apiClient.infoRecipient().getPGGroupsV1()
                )
                .tests();
    }
}

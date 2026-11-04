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
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Endpoint della platea <strong>impresa PG</strong> (chiavi di integrazione e consensi PG): una PA
 * e una persona fisica non devono potervi accedere, e la PG stessa non deve esserne respinta.
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

    /** Utenza della platea PG: la controprova verifica che non venga respinta. */
    private static final List<UserAlias> ALLOWED_PG_USERS = List.of(UserAlias.IMPRESA_2);

    private static final String UNKNOWN_KID = "54a21d3c-2f17-4e11-9285-fe10408f56sa";

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /** {@code PublicKeys}: 6 endpoint. */
    @TestFactory
    Stream<DynamicTest> publicKeysAreForbiddenOutsidePg() {
        return validator.as(NON_PG_USERS).apiCalls(publicKeysApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> publicKeysAreAllowedForPg() {
        return validator.as(ALLOWED_PG_USERS).apiCalls(publicKeysApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] publicKeysApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.publicKeys().getPublicKeysV1(),
                () -> apiClient.publicKeys().newPublicKeyV1(),
                () -> apiClient.publicKeys().checkIssuerPublicKeyV1(),
                () -> apiClient.publicKeys().deletePublicKeyV1().kidPath(UNKNOWN_KID),
                () -> apiClient.publicKeys().rotatePublicKeyV1().kidPath(UNKNOWN_KID),
                () -> apiClient.publicKeys().changeStatusPublicKeyV1().kidPath(UNKNOWN_KID)
        };
    }

    /** {@code VirtualKeys}: 4 endpoint. */
    @TestFactory
    Stream<DynamicTest> virtualKeysAreForbiddenOutsidePg() {
        return validator.as(NON_PG_USERS).apiCalls(virtualKeysApiCalls()).tests();
    }

    /* Il test relativo alla deleteVirtualKeyV1 attualmente ritorna un 403
    * per via di un bug aperto: https://pagopa.atlassian.net/browse/PN-22255
    */
    @TestFactory
    Stream<DynamicTest> virtualKeysAreAllowedForPg() {
        return validator.as(ALLOWED_PG_USERS).apiCalls(virtualKeysApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] virtualKeysApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.virtualKeys().getVirtualKeysV1(),
                () -> apiClient.virtualKeys().newVirtualKeyV1(),
                () -> apiClient.virtualKeys().deleteVirtualKeyV1().kidPath(UNKNOWN_KID),
                () -> apiClient.virtualKeys().changeStatusVirtualKeysV1().kidPath(UNKNOWN_KID)
        };
    }

    /** Consensi e gruppi specifici PG: 3 endpoint. */
    @TestFactory
    Stream<DynamicTest> pgConsentsAndGroupsAreForbiddenOutsidePg() {
        return validator.as(NON_PG_USERS).apiCalls(pgConsentsAndGroupsApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> pgConsentsAndGroupsAreAllowedForPg() {
        return validator.as(ALLOWED_PG_USERS)
                .apiCalls(pgConsentsAndGroupsApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] pgConsentsAndGroupsApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.userConsents().getPgTosPrivacyV1(),
                () -> apiClient.userConsents().acceptPgTosPrivacyV1(),
                () -> apiClient.infoRecipient().getPGGroupsV1()
        };
    }
}

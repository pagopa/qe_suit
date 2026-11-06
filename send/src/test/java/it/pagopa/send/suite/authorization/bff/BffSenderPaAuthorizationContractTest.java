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
 * Endpoint della platea <strong>mittente PA</strong>: le utenze fuori da quella platea non devono
 * potervi accedere, e la PA stessa non deve esserne respinta.
 * <p>
 * Il controllo dei permessi precede la validazione del payload, quindi i supplier non preparano
 * body validi ne' identificativi esistenti: un {@code 400} o un {@code 404} al posto del
 * {@code 403} e' un fallimento, non un esito alternativo accettabile.
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
public class BffSenderPaAuthorizationContractTest {

    /** Utenze fuori dalla platea PA. */
    private static final List<UserAlias> NON_PA_USERS = List.of(UserAlias.USER_1, UserAlias.IMPRESA_1);

    /** Utenza della platea PA: la controprova verifica che non venga respinta. */
    private static final List<UserAlias> ALLOWED_PA_USERS = List.of(UserAlias.COMUNE_2);

    private static final String UNKNOWN_IUN = "KNDA-NPAG-VANA-202502-J-A";
    private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /** {@code NotificationSent}: 8 endpoint. */
    @TestFactory
    Stream<DynamicTest> notificationSentIsForbiddenOutsidePa() {
        return validator.as(NON_PA_USERS).apiCalls(notificationSentApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> notificationSentIsAllowedForPa() {
        return validator.as(ALLOWED_PA_USERS).apiCalls(notificationSentApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] notificationSentApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.notificationSent().searchSentNotificationsV1(),
                () -> apiClient.notificationSent().newSentNotificationV1(),
                () -> apiClient.notificationSent().preSignedUploadV1(),
                () -> apiClient.notificationSent().getSentNotificationV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationSent().notificationCancellationV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationSent().getSentNotificationTimelineV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationSent().getSentNotificationDocumentV1()
                        .iunPath(UNKNOWN_IUN)
                        .documentTypePath("AAR"),
                () -> apiClient.notificationSent().getSentNotificationPaymentV1()
                        .iunPath(UNKNOWN_IUN)
                        .recipientIdxPath(0)
                        .attachmentNamePath("PAGOPA")
        };
    }

    /** {@code ApiKeys}: 4 endpoint. */
    @TestFactory
    Stream<DynamicTest> apiKeysAreForbiddenOutsidePa() {
        return validator.as(NON_PA_USERS).apiCalls(apiKeysApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> apiKeysAreAllowedForPa() {
        return validator.as(ALLOWED_PA_USERS).apiCalls(apiKeysApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] apiKeysApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.apiKeys().getApiKeysV1(),
                () -> apiClient.apiKeys().newApiKeyV1(),
                () -> apiClient.apiKeys().deleteApiKeyV1().idPath(UNKNOWN_ID),
                () -> apiClient.apiKeys().changeStatusApiKeyV1().idPath(UNKNOWN_ID)
        };
    }

    /** {@code InfoPa}: 5 endpoint. */
    @TestFactory
    Stream<DynamicTest> paInformationIsForbiddenOutsidePa() {
        return validator.as(NON_PA_USERS).apiCalls(infoPaApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> paInformationIsAllowedForPa() {
        return validator.as(ALLOWED_PA_USERS).apiCalls(infoPaApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] infoPaApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.infoPa().getInstitutionsV1(),
                () -> apiClient.infoPa().getInstitutionProductsV1(),
                () -> apiClient.infoPa().getPAGroupsV1(),
                () -> apiClient.infoPa().getAdditionalLang(),
                () -> apiClient.infoPa().changeAdditionalLang()
        };
    }

    /** {@code SenderDashboard}: 1 endpoint. */
    @TestFactory
    Stream<DynamicTest> senderDashboardIsForbiddenOutsidePa() {
        return validator.as(NON_PA_USERS).apiCalls(senderDashboardApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> senderDashboardIsAllowedForPa() {
        return validator.as(ALLOWED_PA_USERS).apiCalls(senderDashboardApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] senderDashboardApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.senderDashboard().getDashboardDataV1()
                        .cxTypePath("PA")
                        .cxIdPath(UNKNOWN_ID)
        };
    }

    /** {@code SenderInformalNotifications}: 7 endpoint. */
    @TestFactory
    Stream<DynamicTest> senderInformalNotificationsAreForbiddenOutsidePa() {
        return validator.as(NON_PA_USERS).apiCalls(senderInformalNotificationsApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> senderInformalNotificationsAreAllowedForPa() {
        return validator.as(ALLOWED_PA_USERS)
                .apiCalls(senderInformalNotificationsApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] senderInformalNotificationsApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.senderInformalNotifications().getListCampaignsV1(),
                () -> apiClient.senderInformalNotifications().getCampaignDetailV1()
                        .campaignIdPath(UNKNOWN_ID),
                () -> apiClient.senderInformalNotifications().searchInformalSentNotificationV1()
                        .campaignIdPath(UNKNOWN_ID),
                () -> apiClient.senderInformalNotifications().getSentInformalNotificationV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.senderInformalNotifications().getSentInformalNotificationTimelineV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.senderInformalNotifications().getSentInformalNotificationDocumentV1()
                        .iunPath(UNKNOWN_IUN)
                        .docIdxPath(0),
                () -> apiClient.senderInformalNotifications().getSentInformalNotificationAttachmentV1()
                        .iunPath(UNKNOWN_IUN)
                        .recipientIdxPath(0)
                        .attachmentNamePath("PAGOPA")
        };
    }
}

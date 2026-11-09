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
 * Endpoint della platea <strong>destinatario</strong>: una PA non deve potervi accedere, e un
 * destinatario legittimo (PF o PG) non deve esserne respinto.
 * <p>
 * Le utenze respinte dai gruppi {@code ForbiddenForPa} sono solo quelle PA: in SEND anche
 * un'impresa puo' essere destinataria di una notifica, quindi attendersi un {@code 403} per una
 * PG su questi endpoint sarebbe sbagliato. Alcuni endpoint sono riservati a un solo tipo di
 * destinatario (PF o PG): per quelli ci sono coppie di test dedicate che verificano l'esclusione
 * dall'altro tipo, oltre all'accesso di quello corretto.
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
public class BffRecipientAuthorizationContractTest {

    /** Utenze fuori dalla platea destinatario. */
    private static final List<UserAlias> PA_USERS = List.of(UserAlias.COMUNE_1);

    /** Utenze della platea destinatario: la controprova verifica che non vengano respinte. */
    private static final List<UserAlias> ALLOWED_RECIPIENT_USERS =
            List.of(UserAlias.USER_2, UserAlias.IMPRESA_2);

    private static final List<UserAlias> PF_USERS = List.of(UserAlias.USER_2);
    private static final List<UserAlias> PG_USERS = List.of(UserAlias.IMPRESA_2);

    private static final String UNKNOWN_IUN = "KNDA-NPAG-VANA-202502-J-A";
    private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /** {@code NotificationReceived}: 8 endpoint, di cui 6 condivisi tra PF e PG. */
    @TestFactory
    Stream<DynamicTest> receivedNotificationsAreForbiddenForPa() {
        return validator.as(PA_USERS).apiCalls(notificationReceivedApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> receivedNotificationsAreAllowedForRecipients() {
        return validator.as(ALLOWED_RECIPIENT_USERS)
                .apiCalls(notificationReceivedSharedApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] notificationReceivedApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.notificationReceived().searchReceivedNotificationsV1(),
                () -> apiClient.notificationReceived().searchReceivedDelegatedNotificationsV1(),
                () -> apiClient.notificationReceived().checkAarQrCodeV1(),
                () -> apiClient.notificationReceived().checkTppV1(),
                () -> apiClient.notificationReceived().getReceivedNotificationV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationReceived().getReceivedNotificationTimelineV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationReceived().getReceivedNotificationDocumentV1()
                        .iunPath(UNKNOWN_IUN)
                        .documentTypePath("AAR"),
                () -> apiClient.notificationReceived().getReceivedNotificationPaymentV1()
                        .iunPath(UNKNOWN_IUN)
                        .attachmentNamePath("PAGOPA")
        };
    }

    /** I 6 endpoint di {@code NotificationReceived} aperti sia a PF sia a PG. */
    private Supplier<?>[] notificationReceivedSharedApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.notificationReceived().searchReceivedNotificationsV1(),
                () -> apiClient.notificationReceived().checkAarQrCodeV1(),
                () -> apiClient.notificationReceived().getReceivedNotificationV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationReceived().getReceivedNotificationTimelineV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.notificationReceived().getReceivedNotificationDocumentV1()
                        .iunPath(UNKNOWN_IUN)
                        .documentTypePath("AAR"),
                () -> apiClient.notificationReceived().getReceivedNotificationPaymentV1()
                        .iunPath(UNKNOWN_IUN)
                        .attachmentNamePath("PAGOPA")
        };
    }

    /** {@code searchReceivedDelegatedNotificationsV1} e' riservato alla PG. */
    @TestFactory
    Stream<DynamicTest> receivedDelegatedNotificationsIsForbiddenForPf() {
        return validator.as(PF_USERS).apiCalls(receivedDelegatedNotificationsApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> receivedDelegatedNotificationsIsAllowedForPg() {
        return validator.as(PG_USERS)
                .apiCalls(receivedDelegatedNotificationsApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] receivedDelegatedNotificationsApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.notificationReceived().searchReceivedDelegatedNotificationsV1()
        };
    }

    /** {@code checkTppV1} e' riservato alla PF. */
    @TestFactory
    Stream<DynamicTest> checkTppIsForbiddenForPg() {
        return validator.as(PG_USERS).apiCalls(checkTppApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> checkTppIsAllowedForPf() {
        return validator.as(PF_USERS).apiCalls(checkTppApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] checkTppApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.notificationReceived().checkTppV1()
        };
    }

    /** {@code RecipientInformalNotifications}: 3 endpoint, tutti condivisi tra PF e PG. */
    @TestFactory
    Stream<DynamicTest> receivedInformalNotificationsAreForbiddenForPa() {
        return validator.as(PA_USERS).apiCalls(recipientInformalNotificationsApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> receivedInformalNotificationsAreAllowedForRecipients() {
        return validator.as(ALLOWED_RECIPIENT_USERS)
                .apiCalls(recipientInformalNotificationsApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] recipientInformalNotificationsApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.recipientInformalNotifications().getReceivedInformalNotificationV1()
                        .iunPath(UNKNOWN_IUN),
                () -> apiClient.recipientInformalNotifications().getReceivedInformalNotificationDocumentV1()
                        .iunPath(UNKNOWN_IUN)
                        .docIdxPath(0),
                () -> apiClient.recipientInformalNotifications()
                        .getReceivedInformalNotificationPaymentAttachmentV1()
                        .iunPath(UNKNOWN_IUN)
                        .attachmentNamePath("PAGOPA")
        };
    }

    /** {@code Addresses}: 3 endpoint, tutti condivisi tra PF e PG. */
    @TestFactory
    Stream<DynamicTest> addressesAreForbiddenForPa() {
        return validator.as(PA_USERS).apiCalls(addressesApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> addressesAreAllowedForRecipients() {
        return validator.as(ALLOWED_RECIPIENT_USERS).apiCalls(addressesApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] addressesApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.addresses().getAddressesV1(),
                () -> apiClient.addresses().createOrUpdateAddressV1()
                        .addressTypePath("LEGAL")
                        .senderIdPath("default")
                        .channelTypePath("PEC"),
                () -> apiClient.addresses().deleteAddressV1()
                        .addressTypePath("LEGAL")
                        .senderIdPath(UNKNOWN_ID)
                        .channelTypePath("PEC")
        };
    }

    /** {@code Mandate}: 9 endpoint, di cui 5 condivisi tra PF e PG. */
    @TestFactory
    Stream<DynamicTest> mandatesAreForbiddenForPa() {
        return validator.as(PA_USERS).apiCalls(mandateApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> mandatesAreAllowedForRecipients() {
        return validator.as(ALLOWED_RECIPIENT_USERS)
                .apiCalls(mandateSharedApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] mandateApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.mandate().createMandateV1(),
                () -> apiClient.mandate().getMandatesByDelegateV1(),
                () -> apiClient.mandate().searchMandatesByDelegateV1(),
                () -> apiClient.mandate().countMandatesByDelegateV1(),
                () -> apiClient.mandate().getMandatesByDelegatorV1(),
                () -> apiClient.mandate().acceptMandateV1().mandateIdPath(UNKNOWN_ID),
                () -> apiClient.mandate().rejectMandateV1().mandateIdPath(UNKNOWN_ID),
                () -> apiClient.mandate().revokeMandateV1().mandateIdPath(UNKNOWN_ID),
                () -> apiClient.mandate().updateMandateV1().mandateIdPath(UNKNOWN_ID)
        };
    }

    /** I 5 endpoint di {@code Mandate} aperti sia a PF sia a PG. */
    private Supplier<?>[] mandateSharedApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.mandate().createMandateV1(),
                () -> apiClient.mandate().getMandatesByDelegatorV1(),
                () -> apiClient.mandate().acceptMandateV1().mandateIdPath(UNKNOWN_ID),
                () -> apiClient.mandate().rejectMandateV1().mandateIdPath(UNKNOWN_ID),
                () -> apiClient.mandate().revokeMandateV1().mandateIdPath(UNKNOWN_ID)
        };
    }

    /** {@code getMandatesByDelegateV1} e' riservato alla PF. */
    @TestFactory
    Stream<DynamicTest> mandatesByDelegateIsForbiddenForPg() {
        return validator.as(PG_USERS).apiCalls(mandatesByDelegateApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> mandatesByDelegateIsAllowedForPf() {
        return validator.as(PF_USERS).apiCalls(mandatesByDelegateApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] mandatesByDelegateApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.mandate().getMandatesByDelegateV1()
        };
    }

    /**
     * {@code searchMandatesByDelegateV1}, {@code countMandatesByDelegateV1} e
     * {@code updateMandateV1} sono riservati alla PG.
     */
    @TestFactory
    Stream<DynamicTest> pgOnlyMandateOperationsAreForbiddenForPf() {
        return validator.as(PF_USERS).apiCalls(pgOnlyMandateApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> pgOnlyMandateOperationsAreAllowedForPg() {
        return validator.as(PG_USERS).apiCalls(pgOnlyMandateApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] pgOnlyMandateApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.mandate().searchMandatesByDelegateV1(),
                () -> apiClient.mandate().countMandatesByDelegateV1(),
                () -> apiClient.mandate().updateMandateV1().mandateIdPath(UNKNOWN_ID)
        };
    }

    /** {@code Payments}: 3 endpoint, di cui 2 condivisi tra PF e PG. */
    @TestFactory
    Stream<DynamicTest> paymentsAreForbiddenForPa() {
        return validator.as(PA_USERS).apiCalls(paymentsApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> paymentsAreAllowedForRecipients() {
        return validator.as(ALLOWED_RECIPIENT_USERS)
                .apiCalls(paymentsSharedApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] paymentsApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.payments().getPaymentsInfoV1(),
                () -> apiClient.payments().paymentsCartV1(),
                () -> apiClient.payments().paymentsTppV1()
        };
    }

    /** I 2 endpoint di {@code Payments} aperti sia a PF sia a PG. */
    private Supplier<?>[] paymentsSharedApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.payments().getPaymentsInfoV1(),
                () -> apiClient.payments().paymentsCartV1()
        };
    }

    /** {@code paymentsTppV1} e' riservato alla PF. */
    @TestFactory
    Stream<DynamicTest> paymentsTppIsForbiddenForPg() {
        return validator.as(PG_USERS).apiCalls(paymentsTppApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> paymentsTppIsAllowedForPf() {
        return validator.as(PF_USERS).apiCalls(paymentsTppApiCalls()).expectedStatusNot(403).tests();
    }

    private Supplier<?>[] paymentsTppApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.payments().paymentsTppV1()
        };
    }

    /** {@code InfoRecipient}, lato destinatario: 1 endpoint condiviso tra PF e PG. */
    @TestFactory
    Stream<DynamicTest> recipientPaListIsForbiddenForPa() {
        return validator.as(PA_USERS).apiCalls(infoRecipientApiCalls()).tests();
    }

    @TestFactory
    Stream<DynamicTest> recipientPaListIsAllowedForRecipients() {
        return validator.as(ALLOWED_RECIPIENT_USERS)
                .apiCalls(infoRecipientApiCalls())
                .expectedStatusNot(403)
                .tests();
    }

    private Supplier<?>[] infoRecipientApiCalls() {
        return new Supplier<?>[]{
                () -> apiClient.infoRecipient().getPAListV1()
        };
    }
}

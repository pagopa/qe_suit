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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Endpoint della platea <strong>destinatario</strong>: una PA non deve potervi accedere.
 * <p>
 * Le utenze respinte sono solo quelle PA: in SEND anche un'impresa puo' essere destinataria di
 * una notifica, quindi attendersi un {@code 403} per una PG su questi endpoint sarebbe sbagliato.
 */
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, BffApiContractConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("junit")
@RequiredArgsConstructor
public class BffRecipientAuthorizationContractTest {

    /**
     * Utenze fuori dalla platea destinatario: due PA.
     */
    private static final List<UserAlias> PA_USERS = List.of(UserAlias.COMUNE_1, UserAlias.COMUNE_2);

    private static final String UNKNOWN_IUN = "AAAA-BBBB-CCCC-202401-D-1";
    private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;

    /**
     * {@code NotificationReceived}: 8 operazioni x 2 utenze = 16 test.
     */
    @TestFactory
    Stream<DynamicTest> receivedNotificationsAreForbiddenForPa() {
        return validator
                .as(PA_USERS)
                .apiCalls(
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
                )
                .tests();
    }

    /**
     * {@code RecipientInformalNotifications}: 3 operazioni x 2 utenze = 6 test.
     */
    @TestFactory
    Stream<DynamicTest> receivedInformalNotificationsAreForbiddenForPa() {
        return validator
                .as(PA_USERS)
                .apiCalls(
                        () -> apiClient.recipientInformalNotifications().getReceivedInformalNotificationV1()
                                .iunPath(UNKNOWN_IUN),
                        () -> apiClient.recipientInformalNotifications().getReceivedInformalNotificationDocumentV1()
                                .iunPath(UNKNOWN_IUN)
                                .docIdxPath(0),
                        () -> apiClient.recipientInformalNotifications()
                                .getReceivedInformalNotificationPaymentAttachmentV1()
                                .iunPath(UNKNOWN_IUN)
                                .attachmentNamePath("PAGOPA")
                )
                .tests();
    }

    /**
     * {@code Addresses}: 3 operazioni x 2 utenze = 6 test.
     */
    @TestFactory
    Stream<DynamicTest> addressesAreForbiddenForPa() {
        return validator
                .as(PA_USERS)
                .apiCalls(
                        () -> apiClient.addresses().getAddressesV1(),
                        () -> apiClient.addresses().createOrUpdateAddressV1()
                                .addressTypePath("LEGAL")
                                .senderIdPath("default")
                                .channelTypePath("PEC"),
                        () -> apiClient.addresses().deleteAddressV1()
                                .addressTypePath("LEGAL")
                                .senderIdPath("default")
                                .channelTypePath("PEC")
                )
                .tests();
    }

    /**
     * {@code Mandate}: 9 operazioni x 2 utenze = 18 test.
     */
    @TestFactory
    Stream<DynamicTest> mandatesAreForbiddenForPa() {
        return validator
                .as(PA_USERS)
                .apiCalls(
                        () -> apiClient.mandate().createMandateV1(),
                        () -> apiClient.mandate().getMandatesByDelegateV1(),
                        () -> apiClient.mandate().searchMandatesByDelegateV1(),
                        () -> apiClient.mandate().countMandatesByDelegateV1(),
                        () -> apiClient.mandate().getMandatesByDelegatorV1(),
                        () -> apiClient.mandate().acceptMandateV1().mandateIdPath(UNKNOWN_ID),
                        () -> apiClient.mandate().rejectMandateV1().mandateIdPath(UNKNOWN_ID),
                        () -> apiClient.mandate().revokeMandateV1().mandateIdPath(UNKNOWN_ID),
                        () -> apiClient.mandate().updateMandateV1().mandateIdPath(UNKNOWN_ID)
                )
                .tests();
    }

    /**
     * {@code Payments}: 3 operazioni x 2 utenze = 6 test.
     */
    @TestFactory
    Stream<DynamicTest> paymentsAreForbiddenForPa() {
        return validator
                .as(PA_USERS)
                .apiCalls(
                        () -> apiClient.payments().getPaymentsInfoV1(),
                        () -> apiClient.payments().paymentsCartV1(),
                        () -> apiClient.payments().paymentsTppV1()
                )
                .tests();
    }

    /**
     * {@code InfoRecipient}, lato destinatario: 1 operazione x 2 utenze = 2 test.
     */
    @TestFactory
    Stream<DynamicTest> recipientPaListIsForbiddenForPa() {
        return validator
                .as(PA_USERS)
                .apiCalls(() -> apiClient.infoRecipient().getPAListV1())
                .tests();
    }
}
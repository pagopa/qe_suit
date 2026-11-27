package it.pagopa.send.common.journey.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationType;
import it.pagopa.send.common.legal_notification.domain.PhysicalCommunicationKind;
import it.pagopa.send.common.legal_notification.domain.RecipientCardinality;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationCreationCommandFactory;
import it.pagopa.send.common.user.domain.UserType;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class LegalNotificationJourneySteps {

    private static final String DIGITAL_DOMICILE_KEY = "digitalDomicile";
    private static final String TEST_DIGITAL_DOMICILE = "test@pec.it";
    private static final Map<String, String> NO_PAYMENTS = Map.of("pagoPA_number", "0", "F24_number", "0");

    private final SendJourney sendJourney;
    private final LegalNotificationCreationCommandFactory commandFactory;
    private final CurrentUserSession currentUserSession;

    @Given("una notifica di tipo {legalNotificationType} creata dalla PA {user} per il destinatario {user} in stato {string}")
    public void createLegalNotification(LegalNotificationType type, User sender, User recipient, String targetStatus) {
        sendJourney
                .prepareNotification(commandFactory.forLegalNotificationType(sender, type, recipient))
                .sendNotification(sender, NotificationStatus.fromValue(targetStatus));
    }

    @Given("la PA {user} ha creato ed annullato una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void createAndCancelLegalNotification(User sender, LegalNotificationType type, User recipient) {
        sendJourney
                .prepareNotification(commandFactory.forLegalNotificationType(sender, type, recipient))
                .sendNotification(sender, NotificationStatus.ACCEPTED)
                .deleteNotification();
    }

    @Given("una notifica creata dalla PA {user} di tipo {physicalCommunicationKind} per il destinatario {user}")
    public void createNotificationWithRecipient(User sender, PhysicalCommunicationKind kind, User recipient) {
        LegalNotificationCreationCommand command = commandFactory.forExplicitRecipients(sender, Map.of(recipient, Map.of()))
                .physicalCommunication(kind.physicalCommunication());
        sendJourney.prepareNotification(command).sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @Given("una notifica {recipientCardinality} creata dalla PA {user} di tipo {physicalCommunicationKind} con destinatario {userType}")
    public void createNotificationWithIndifferentRecipient(RecipientCardinality cardinality, User sender,
                                                           PhysicalCommunicationKind kind, UserType recipientType) {
        LegalNotificationCreationCommand command = commandFactory.forGeneratedRecipients(sender, recipientType, cardinality)
                .physicalCommunication(kind.physicalCommunication());
        sendJourney.prepareNotification(command).sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @Given("una notifica {recipientCardinality} creata dalla PA {user} con destinatario di tipo {userType}")
    public void createNotificationWithGeneratedRecipients(RecipientCardinality cardinality, User sender, UserType recipientType) {
        LegalNotificationCreationCommand command = commandFactory.forGeneratedRecipients(sender, recipientType, cardinality);
        sendJourney.prepareNotification(command).sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @Given("una notifica {recipientCardinality} creata dalla PA {user} con destinatario di tipo {userType} con domicilio digitale")
    public void createNotificationWithDigitalDomicile(RecipientCardinality cardinality, User sender, UserType recipientType) {
        LegalNotificationCreationCommand command = commandFactory.forGeneratedRecipients(sender, recipientType, cardinality,
                Map.of(DIGITAL_DOMICILE_KEY, TEST_DIGITAL_DOMICILE));
        sendJourney.prepareNotification(command).sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    /** Un destinatario persona fisica e uno persona giuridica, senza pagamenti. */
    @Given("una notifica multidestinatario mista creata dalla PA {user}")
    public void createMixedRecipientsNotification(User sender) {
        LegalNotificationCreationCommand command = commandFactory.forMixedRecipients(sender, NO_PAYMENTS);
        sendJourney.prepareNotification(command).sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @When("l'ente {user} crea una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void createNotificationBySender(User sender, LegalNotificationType type, User recipient) {
        sendJourney.prepareNotification(commandFactory.forLegalNotificationType(sender, type, recipient))
                .sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @When("l'ente crea una notifica di tipo {legalNotificationType} per il destinatario {user}")
    public void createNotificationBySessionSender(LegalNotificationType type, User recipient) {
        User sender = currentUserSession.getUser();
        sendJourney.prepareNotification(commandFactory.forLegalNotificationType(sender, type, recipient))
                .sendNotification(sender, NotificationStatus.ACCEPTED);
    }

    @When("l'ente {user} crea una notifica di tipo {legalNotificationType} per il destinatario {user} con i seguenti valori:")
    public void createNotificationWithRecipientValues(User sender, LegalNotificationType type, User recipient, DataTable overrides) {
        sendJourney.prepareNotification(commandFactory.forLegalNotificationType(sender, type, recipient, overrides.asMap(String.class, String.class)))
                .sendNotification(sender, NotificationStatus.ACCEPTED);
    }
}

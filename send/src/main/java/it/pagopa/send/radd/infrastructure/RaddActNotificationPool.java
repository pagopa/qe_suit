package it.pagopa.send.radd.infrastructure;

import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.legal_notification.domain.FeePolicy;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.PhysicalCommunicationType;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.legal_notification.infrastructure.factory.RecipientSpecFactory;
import it.pagopa.send.common.radd.domain.RaddTransaction;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Notifica precondizione del caso d'uso ACT: inviata da {@code COMUNE_1} alla persona fisica {@code USER_1}, e
 * riusata finche' si puo'. Una {@code completeActTransaction} la consuma (le start successive rispondono code 3,
 * "stampa gia' eseguita"): solo allora se ne invia una nuova, cosi' non serve una notifica per ogni caso di test.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RaddActNotificationPool {

    public static final UserAlias SENDER = UserAlias.COMUNE_1;
    public static final UserAlias RECIPIENT = UserAlias.USER_1;

    private static final String SUBJECT = "Notifica di test per i contract test RADD";
    private static final int MAX_START_ATTEMPTS = 2;

    private final SendJourney sendJourney;
    private final RecipientSpecFactory recipientSpecFactory;
    private final UserProvider userProvider;

    private String iun;

    public synchronized String currentIun() {
        if (iun == null) {
            iun = sendNotification();
        }
        return iun;
    }

    public String recipientTaxId() {
        return userProvider.resolve(RECIPIENT).getTaxId();
    }

    /**
     * Transazione ACT avviata (code 0) sulla notifica corrente; se la notifica risulta gia' stampata se ne invia una
     * nuova e si riprova una volta.
     */
    public synchronized RaddTransaction startedActTransaction() {
        RaddTransaction transaction = null;
        for (int attempt = 0; attempt < MAX_START_ATTEMPTS; attempt++) {
            transaction = sendJourney
                    .startActTransaction(currentIun(), recipientTaxId())
                    .get(RaddTransaction.class);
            if (transaction.started()) {
                return transaction;
            }
            if (!transaction.alreadyPrinted()) {
                break;
            }
            log.info("Notifica {} gia' stampata via RADD: ne invio una nuova", iun);
            iun = null;
        }
        throw new IllegalStateException("Transazione ACT non avviata sulla notifica " + iun
                + ": status.code " + (transaction == null ? "n/d" : transaction.code()));
    }

    private String sendNotification() {
        User sender = userProvider.resolve(SENDER);
        RecipientSpec recipientSpec = recipientSpecFactory.build(userProvider.resolve(RECIPIENT), Map.of(
                "physicalAddress_address", "Via@ok_890",
                "physicalAddress_municipality", "COLLELUNGO",
                "physicalAddress_province", "TR",
                "physicalAddress_zip", "05010",
                "pagoPA_number", "0",
                "F24_number", "0"
        ));
        String created = sendJourney
                .prepareNotification(new LegalNotificationCreationCommand()
                        .sender(sender)
                        .subject(SUBJECT)
                        .physicalCommunication(PhysicalCommunicationType.REGISTERED_LETTER_890)
                        .feePolicy(FeePolicy.FLAT_RATE)
                        .recipient(recipientSpec))
                .sendNotification(sender, NotificationStatus.ACCEPTED)
                .get(LegalNotificationDomain.class)
                .getIun();
        log.info("Notifica {} inviata come precondizione RADD ACT", created);
        return created;
    }
}

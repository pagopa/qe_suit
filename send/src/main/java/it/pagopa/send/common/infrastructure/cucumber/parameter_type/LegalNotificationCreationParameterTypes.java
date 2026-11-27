package it.pagopa.send.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.PhysicalCommunicationKind;
import it.pagopa.send.common.legal_notification.domain.RecipientCardinality;
import it.pagopa.send.common.user.domain.UserType;

/**
 * Vocabolario degli step sintetici di creazione di una notifica legale:
 * <ul>
 *   <li>{@code {physicalCommunicationKind}}: tipologia di comunicazione fisica, {@code AR} oppure {@code 890};</li>
 *   <li>{@code {recipientCardinality}}: numero di destinatari generati, {@code monodestinatario} oppure
 *   {@code multidestinatario};</li>
 *   <li>{@code {userType}}: tipologia dei destinatari generati, {@code PF} oppure {@code PG}. Non va confuso con
 *   {@code {recipientType}} di {@link UserParameterTypes}, che negli step di accesso indica il destinatario con
 *   l'articolo ("la PG", "l'utente").</li>
 *   <li>{@code {notificationStatus}}: stato della notifica, tra virgolette, con la descrizione mostrata all'utente
 *   ({@code "Perfezionata per decorrenza termini"}) al posto del valore tecnico ({@code EFFECTIVE_DATE}).</li>
 * </ul>
 */
public class LegalNotificationCreationParameterTypes {

    @ParameterType("AR|890")
    public PhysicalCommunicationKind physicalCommunicationKind(String value) {
        return "AR".equals(value) ? PhysicalCommunicationKind.AR : PhysicalCommunicationKind.NOTICE_890;
    }

    @ParameterType("monodestinatario|multidestinatario")
    public RecipientCardinality recipientCardinality(String value) {
        return "monodestinatario".equals(value) ? RecipientCardinality.SINGLE : RecipientCardinality.MULTI;
    }

    @ParameterType("PF|PG")
    public UserType userType(String value) {
        return UserType.valueOf(value);
    }

    @ParameterType("\"([^\"]*)\"")
    public NotificationStatus notificationStatus(String label) {
        return NotificationStatus.fromLabel(label)
                .orElseThrow(() -> new IllegalArgumentException("Nessuno stato della notifica con la descrizione: " + label));
    }
}

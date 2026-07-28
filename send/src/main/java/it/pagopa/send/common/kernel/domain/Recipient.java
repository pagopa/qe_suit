package it.pagopa.send.common.kernel.domain;

import it.pagopa.send.generated.openapi.clients.bff.model.NotificationRecipientV24;
import lombok.Getter;

import static it.pagopa.send.common.kernel.domain.UserType.PA;
import static it.pagopa.send.common.kernel.domain.UserType.PF;
import static it.pagopa.send.common.kernel.domain.UserType.PG;

public enum Recipient {
    PETRARCA(
            PG,
            "FrancescoPetrarca",
            "test",
            "Le Epistolae srl",
            "LELPTR04A01C352E",
            "Francesco Petrarca"
    ),
    LUCREZIA(PF,
            "lucrezia",
            "password123",
            null,
            "BRGLRZ80D58H501Q",
            "Lucrezia"
    );

    @Getter
    private final UserType type;
    @Getter
    private final String username;
    @Getter
    private final String password;
    @Getter
    private final String organization;
    @Getter
    private final String taxId;
    @Getter
    private final String denomination;

    Recipient(UserType type, String username, String password, String organization, String taxId, String denomination) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.organization = organization;
        this.taxId = taxId;
        this.denomination = denomination;
    }

    /**
     * Tipo destinatario nel formato richiesto da {@code NotificationRecipientV24}: un {@link Recipient}
     * usato come destinatario di una notifica è per costruzione PF o PG, mai PA.
     */
    public NotificationRecipientV24.RecipientTypeEnum recipientType() {
        return switch (type) {
            case PF -> NotificationRecipientV24.RecipientTypeEnum.PF;
            case PG -> NotificationRecipientV24.RecipientTypeEnum.PG;
            case PA -> throw new IllegalStateException(this + " è un utente PA, non può essere destinatario di una notifica");
        };
    }

    public static Recipient fromUsername(String username) {
        for(Recipient u : values()) {
            if (u.username.equalsIgnoreCase(username)) {
                return u;
            }
        }

        throw new IllegalArgumentException("No enum constant found for username: " + username);
    }

}

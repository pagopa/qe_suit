package it.pagopa.send.common.kernel.domain;

import it.pagopa.send.generated.openapi.clients.bff.model.NotificationRecipientV24;
import lombok.Getter;

import static it.pagopa.send.common.kernel.domain.UserType.PF;
import static it.pagopa.send.common.kernel.domain.UserType.PG;

public enum Recipient implements User {
    PETRARCA(
            PG,
            "FrancescoPetrarca",
            "test",
            "Le Epistolae srl",
            "LELPTR04A01C352E",
            "Francesco Petrarca",
            null,
            null
    ),
    LUCREZIA(PF,
            "lucrezia",
            "password123",
            null,
            "BRGLRZ80D58H501Q",
            "Lucrezia",
            null,
            null
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
    @Getter
    private final String organizationId;
    @Getter
    private final String uid;

    Recipient(UserType type, String username, String password, String organization, String taxId,
              String denomination, String organizationId, String uid) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.organization = organization;
        this.taxId = taxId;
        this.denomination = denomination;
        this.organizationId = organizationId;
        this.uid = uid;
    }

    @Override
    public String getName() {
        return this.denomination;
    }

    @Override
    public String getFiscalNumber() {
        return this.taxId;
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

package it.pagopa.send.common.user.domain;

import lombok.Getter;

import java.util.List;

import static it.pagopa.send.common.user.domain.UserType.PF;
import static it.pagopa.send.common.user.domain.UserType.PG;

public enum Recipient implements User {
    PETRARCA(
            PG,
            "FrancescoPetrarca",
            "test",
            "Le Epistolae srl",
            "12666810299",
            "Francesco Petrarca",
            "Petrarca",
            "e2486126-4394-4300-a25d-b6d879fa1717",
            "d5a8a380-910d-49dc-9fb8-84700c64410f",
            "PTRFNC04A01C352E",
            List.of(new OrganizationRole("MANAGER", "pg-admin"))
    ),
    LUCREZIA(PF,
            "lucrezia",
            "password123",
            null,
            "BRGLRZ80D58H501Q",
            "Lucrezia",
            "Borgia",
            null,
            "df2a1268-ecfe-4330-b650-5184a7eb0756",
            null,
            List.of()
    ),
    RENATO(PF,
            "rguttuso-ta",
            "Automation123",
            null,
            "GTTRNT80T25F205T",
            "Renato",
            "Guttuso",
            null,
            null,
            null,
            List.of()
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
    private final String familyName;
    @Getter
    private final String organizationId;
    @Getter
    private final String uid;
    /**
     * Codice fiscale della persona che accede, se diverso da {@link #taxId}: per una PG {@code taxId} è quello
     * dell'impresa, usato come destinatario delle notifiche, mentre la sessione del portale vuole quello personale.
     */
    private final String personalTaxId;
    /**
     * Ruoli SelfCare della persona nell'impresa: il portale PG li legge dalla sessione per decidere le voci visibili
     * (per esempio Deleghe e Recapiti solo all'amministratore).
     */
    @Getter
    private final List<OrganizationRole> roles;

    Recipient(UserType type, String username, String password, String organization, String taxId,
              String denomination, String familyName, String organizationId, String uid, String personalTaxId,
              List<OrganizationRole> roles) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.organization = organization;
        this.taxId = taxId;
        this.denomination = denomination;
        this.familyName = familyName;
        this.organizationId = organizationId;
        this.uid = uid;
        this.personalTaxId = personalTaxId;
        this.roles = roles;
    }

    @Override
    public String getName() {
        return this.denomination;
    }

    @Override
    public String getFiscalNumber() {
        return this.personalTaxId != null ? this.personalTaxId : this.taxId;
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

package it.pagopa.send.common.user.domain;

import lombok.Getter;

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
            null,
            null
    ),
    LUCREZIA(PF,
            "lucrezia",
            "password123",
            null,
            "BRGLRZ80D58H501Q",
            "Lucrezia",
            "Borgia",
            null,
            "df2a1268-ecfe-4330-b650-5184a7eb0756"
    ),
    RENATO(PF,
            "rguttuso-ta",
            "Automation123",
            null,
            "GTTRNT80T25F205T",
            "Renato",
            "Guttuso",
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
    private final String familyName;
    @Getter
    private final String organizationId;
    @Getter
    private final String uid;

    Recipient(UserType type, String username, String password, String organization, String taxId,
              String denomination, String familyName, String organizationId, String uid) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.organization = organization;
        this.taxId = taxId;
        this.denomination = denomination;
        this.familyName = familyName;
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

    public static Recipient fromUsername(String username) {
        for(Recipient u : values()) {
            if (u.username.equalsIgnoreCase(username)) {
                return u;
            }
        }

        throw new IllegalArgumentException("No enum constant found for username: " + username);
    }

}

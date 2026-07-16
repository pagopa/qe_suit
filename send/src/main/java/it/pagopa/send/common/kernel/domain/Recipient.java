package it.pagopa.send.common.kernel.domain;

import lombok.Getter;

import static it.pagopa.send.common.kernel.domain.UserType.PA;
import static it.pagopa.send.common.kernel.domain.UserType.PF;
import static it.pagopa.send.common.kernel.domain.UserType.PG;

public enum Recipient {
    GROSSINI(
            PA,
            "grossini",
            "test",
            "Comune di Palermo"
    ),
    PETRARCA(
            PG,
            "FrancescoPetrarca",
            "test",
            "Le Epistolae srl"
    ),
    LUCREZIA(PF,
            "lucrezia",
            "password123",
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

    Recipient(UserType type, String username, String password, String organization) {
        this.type = type;
        this.username = username;
        this.password = password;
        this.organization = organization;
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

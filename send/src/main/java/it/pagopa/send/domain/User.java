package it.pagopa.send.domain;

import lombok.Getter;

public enum User {
    GROSSINI("grossini", "test", "Comune di Palermo", "PA"),
    PETRARCA("FrancescoPetrarca", "test", "Le Epistolae srl", "PG"),
    LUCREZIA("lucrezia", "password123", null, "PF"),
    CESARE("cesare", "password123", null, "PF");

    @Getter private final String username;
    @Getter private final String password;
    @Getter private final String organization;
    @Getter private final String type;


    User(String username, String password, String organization, String type) {
        this.username = username;
        this.password = password;
        this.organization = organization;
        this.type = type;
    }

    public static User fromUsername(String username) {
        for (User u : values()) {
            if (u.username.equalsIgnoreCase(username)) {
                return u;
            }
        }
        throw new IllegalArgumentException("No enum constant found for username: " + username);
    }
}

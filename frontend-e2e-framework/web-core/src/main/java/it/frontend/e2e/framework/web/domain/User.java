package it.frontend.e2e.framework.web.domain;

import lombok.Getter;

public enum User {
    GROSSINI("grossini", "test", "Comune di Palermo"),
    PETRARCA("FrancescoPetrarca", "test", "Le Epistolae srl"),
    LUCREZIA("lucrezia", "password123", null);

    @Getter private final String username;
    @Getter private final String password;
    @Getter private final String organization;

    User(String username, String password, String organization) {
        this.username = username;
        this.password = password;
        this.organization = organization;
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

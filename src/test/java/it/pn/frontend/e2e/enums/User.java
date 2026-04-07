package it.pn.frontend.e2e.enums;

import lombok.Getter;

public enum User {
    GROSSINI("grossini", "test", "Comune di Palermo");

    @Getter private final String username;
    @Getter private final String password;
    @Getter private final String comune;

    User(String username, String password, String comune) {
        this.username = username;
        this.password = password;
        this.comune = comune;
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

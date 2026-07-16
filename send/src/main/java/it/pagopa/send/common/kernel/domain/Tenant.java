package it.pagopa.send.common.kernel.domain;

public enum Tenant {
    GROSSINI(
            "grossini",
            "test",
            "Comune di Palermo"
    );

    private final String username;
    private final String password;
    private final String organization;

    private Tenant(String username, String password, String organization) {
        this.username = username;
        this.password = password;
        this.organization = organization;
    }

    public static Tenant fromUsername(String username) {
        for(Tenant u : values()) {
            if (u.username.equalsIgnoreCase(username)) {
                return u;
            }
        }

        throw new IllegalArgumentException("No enum constant found for username: " + username);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getOrganization() {
        return this.organization;
    }
}

package it.pagopa.send.common.kernel.domain;

public enum Tenant {
    GROSSINI(
            "grossini",
            "test",
            "Comune di Palermo",
            "80016350821"
    );

    private final String username;
    private final String password;
    private final String organization;
    private final String taxId;

    private Tenant(String username, String password, String organization, String taxId) {
        this.username = username;
        this.password = password;
        this.organization = organization;
        this.taxId = taxId;
    }

    public static Tenant fromUsername(String username) {
        for(Tenant u : values()) {
            if (u.username.equalsIgnoreCase(username)) {
                return u;
            }
        }

        throw new IllegalArgumentException("No enum constant found for username: " + username);
    }

    public static Tenant fromOrganization(String organization) {
        for(Tenant u : values()) {
            if (u.organization.equalsIgnoreCase(organization)) {
                return u;
            }
        }

        throw new IllegalArgumentException("No enum constant found for organization: " + organization);
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

    public String getTaxId() {
        return this.taxId;
    }
}

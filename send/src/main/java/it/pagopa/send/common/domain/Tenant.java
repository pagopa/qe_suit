package it.pagopa.send.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Tenant implements User {
    GROSSINI(
            "grossini",
            "test",
            "Comune di Palermo",
            "80016350821",
            "giuseppe",
            "RSSGPP80B02G273H",
            "5b994d4a-0fa8-47ac-9c7b-354f1d44a1ce",
            "e9e4a9c7-9586-4b92-a7dd-ee1a0e77d398",
            "grossini@test.cert.it",
            "rossini",
            "c_g273",
            false,
            List.of(new OrganizationRole("SUB_DELEGATE", "admin"))
    );

    private final String username;
    private final String password;
    private final String organization;
    private final String taxId;
    private final String name;
    private final String fiscalNumber;
    private final String organizationId;
    private final String uid;
    private final String email;
    private final String familyName;
    private final String ipaCode;
    private final boolean hasGroups;
    private final List<OrganizationRole> roles;

    @Override
    public UserType getType() {
        return UserType.PA;
    }

    public static Tenant fromUsername(String username) {
        for (Tenant tenant : values()) {
            if (tenant.username.equalsIgnoreCase(username)) {
                return tenant;
            }
        }

        throw new IllegalArgumentException("No enum constant found for username: " + username);
    }

    public static Tenant fromOrganization(String organization) {
        for (Tenant tenant : values()) {
            if (tenant.organization.equalsIgnoreCase(organization)) {
                return tenant;
            }
        }

        throw new IllegalArgumentException("No enum constant found for organization: " + organization);
    }
}

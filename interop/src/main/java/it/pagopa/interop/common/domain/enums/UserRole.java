package it.pagopa.interop.common.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("admin"),
    API("api"),
    SECURITY("security"),
    API_SECURITY("api,security"),
    SUPPORT("support");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public static UserRole fromName(String name) {
        for (UserRole role : UserRole.values()) {
            if (role.getName().equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + name);
    }
}
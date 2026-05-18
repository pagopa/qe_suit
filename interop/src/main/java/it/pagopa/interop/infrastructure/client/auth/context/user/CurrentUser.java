package it.pagopa.interop.infrastructure.client.auth.context.user;

public record CurrentUser(
        String uid,
        String organizationId,
        String selfcareId,
        String userRole
) {
}
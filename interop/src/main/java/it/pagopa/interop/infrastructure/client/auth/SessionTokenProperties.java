package it.pagopa.interop.infrastructure.client.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "interop.session-token")
public record SessionTokenProperties(
        String wellKnownUrl,
        String issuer,
        long durationSec,
        String uid,
        String organizationId,
        String selfcareId,
        String userRole
) {
}
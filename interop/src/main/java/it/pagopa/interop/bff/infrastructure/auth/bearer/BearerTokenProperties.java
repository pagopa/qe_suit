package it.pagopa.interop.bff.infrastructure.auth.bearer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "interop.auth.bearer")
public record BearerTokenProperties(
        String wellKnownUrl,
        String issuer,
        String audience,
        long durationSec
) {
}
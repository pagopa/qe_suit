package it.pagopa.interop.m2m.v3.infrastructure.security;

import it.pagopa.kernel.security.AccessToken;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;

public record M2Mv3AccessTokenContext(
        AccessToken accessToken,
        KeyPair dpopKeyPair,
        Instant expiresAt
) {

    private static final Duration EXPIRATION_SKEW = Duration.ofSeconds(30);

    public boolean isValid() {
        return expiresAt
                .minus(EXPIRATION_SKEW)
                .isAfter(Instant.now());
    }
}
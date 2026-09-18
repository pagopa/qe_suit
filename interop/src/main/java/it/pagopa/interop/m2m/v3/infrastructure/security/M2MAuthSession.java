package it.pagopa.interop.m2m.v3.infrastructure.security;

import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.kernel.security.AccessToken;

import java.security.KeyPair;
import java.time.Instant;

public record M2MAuthSession(
        Client client,
        KeyPair dpopKeyPair,
        AccessToken accessToken,
        Instant expiresAt
) {

    public boolean isTokenValid(Instant now) {
        return accessToken != null
                && expiresAt != null
                && expiresAt.minusSeconds(30).isAfter(now);
    }

    public M2MAuthSession withAccessToken(
            AccessToken accessToken,
            Instant expiresAt
    ) {
        return new M2MAuthSession(
                client,
                dpopKeyPair,
                accessToken,
                expiresAt
        );
    }
}
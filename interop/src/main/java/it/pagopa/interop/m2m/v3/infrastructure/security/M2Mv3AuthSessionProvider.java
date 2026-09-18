package it.pagopa.interop.m2m.v3.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.auth.AccessTokenProvider;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.kernel.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class M2Mv3AuthSessionProvider {

    private static final String CACHE_NAME = "m2mAuthSession";

    @Value("${interop.auth.oauth.server.sync}")
    private String syncHtu;

    private final CacheManager cacheManager;
    private final M2MApiClientProvider apiClientProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final DPoPProofService dPoPProofService;

    public M2MAuthSession getOrCreate(Tenant tenant, M2MRole role) throws NoSuchAlgorithmException, JsonProcessingException {
        String key = cacheKey(tenant, role);
        Cache cache = getCache();
        M2MAuthSession session = cache.get(key, M2MAuthSession.class);

        if (session == null) {
            session = createSession(tenant, role);
            cache.put(key, session);
            return session;
        }

        if (session.isTokenValid(Instant.now()))
            return session;

        M2MAuthSession refreshed = refreshToken(session);
        cache.put(key, refreshed);

        return refreshed;
    }

    private M2MAuthSession createSession(Tenant tenant, M2MRole role) throws NoSuchAlgorithmException, JsonProcessingException {
        Client client = apiClientProvider.getOrCreate(tenant, role);
        KeyPair dpopKeyPair = KeyPairUtils.generate(KeyAlgorithm.EC);

        return issueToken(client, dpopKeyPair);
    }

    private M2MAuthSession refreshToken(M2MAuthSession session) throws JsonProcessingException, NoSuchAlgorithmException {
        return issueToken(session.client(), session.dpopKeyPair());
    }

    private M2MAuthSession issueToken(Client client, KeyPair dpopKeyPair) throws JsonProcessingException, NoSuchAlgorithmException {

        DPoPProof tokenProof =
                dPoPProofService.buildDPoPProof(
                        dpopKeyPair,
                        DPoPProofService.HttpMethod.POST,
                        syncHtu
                );

        Instant issuedAt = Instant.now();

        AccessToken accessToken =
                accessTokenProvider.createAPIAccessToken(
                        client,
                        tokenProof
                );

        Instant expiresAt = issuedAt.plusSeconds(accessToken.expiresIn());

        return new M2MAuthSession(
                client,
                dpopKeyPair,
                accessToken,
                expiresAt
        );
    }

    private Cache getCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        if (cache == null) {
            throw new IllegalStateException(
                    "Cache not configured: " + CACHE_NAME
            );
        }

        return cache;
    }

    private String cacheKey(Tenant tenant, M2MRole role) {
        return tenant.getOrganizationId()
                + "|"
                + role.name();
    }
}
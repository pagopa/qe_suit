package it.pagopa.interop.m2m.v3.infrastructure.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.auth.AccessTokenProvider;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.kernel.security.AccessToken;
import it.pagopa.kernel.security.DPoPProof;
import it.pagopa.kernel.security.DPoPProofService;
import it.pagopa.kernel.security.KeyAlgorithm;
import it.pagopa.kernel.security.KeyPairUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class M2Mv3AccessTokenProvider {

    private static final String CACHE_NAME = "m2mAuthSession";

    @Value("${interop.auth.oauth.server.sync}")
    private String syncHtu;

    private final CacheManager cacheManager;
    private final M2MApiClientProvider apiClientProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final DPoPProofService dPoPProofService;

    private final ConcurrentMap<String, Object> locks = new ConcurrentHashMap<>();

    public M2Mv3AccessTokenContext getOrCreate(Tenant tenant, M2MRole role) throws NoSuchAlgorithmException, JsonProcessingException {
        String key = cacheKey(tenant, role);
        M2Mv3AccessTokenContext cached = getCached(key);

        if (cached != null && cached.isValid()) {
            return cached;
        }

        Object lock = locks.computeIfAbsent(
                key,
                ignored -> new Object()
        );

        synchronized (lock) {
            try {
                /*
                 * Double check:
                 * mentre aspettavamo il lock un altro thread
                 * potrebbe aver già creato/rinnovato il token.
                 */
                cached = getCached(key);

                if (cached != null && cached.isValid()) {
                    return cached;
                }

                Client client =
                        apiClientProvider.getOrCreate(
                                tenant,
                                role
                        );

                M2Mv3AccessTokenContext session;

                if (cached == null) {
                    session = createSession(client);
                } else {
                    session = refreshSession(
                            client,
                            cached
                    );
                }

                getCache().put(key, session);

                return session;

            } finally {
                locks.remove(key, lock);
            }
        }
    }

    private M2Mv3AccessTokenContext createSession(Client client) throws NoSuchAlgorithmException, JsonProcessingException {
        KeyPair dpopKeyPair = KeyPairUtils.generate(KeyAlgorithm.EC);

        return issueToken(
                client,
                dpopKeyPair
        );
    }

    private M2Mv3AccessTokenContext refreshSession(Client client, M2Mv3AccessTokenContext currentSession) throws NoSuchAlgorithmException, JsonProcessingException {
        return issueToken(
                client,
                currentSession.dpopKeyPair()
        );
    }

    private M2Mv3AccessTokenContext issueToken(Client client, KeyPair dpopKeyPair) throws NoSuchAlgorithmException, JsonProcessingException {

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

        Instant expiresAt =
                issuedAt.plusSeconds(
                        accessToken.expiresIn()
                );

        return new M2Mv3AccessTokenContext(
                accessToken,
                dpopKeyPair,
                expiresAt
        );
    }

    private M2Mv3AccessTokenContext getCached(String key) {
        return getCache().get(
                key,
                M2Mv3AccessTokenContext.class
        );
    }

    private Cache getCache() {
        Cache cache = cacheManager.getCache(CACHE_NAME);

        if (cache == null)
            throw new IllegalStateException(
                    "Cache not configured: " + CACHE_NAME
            );

        return cache;
    }

    private String cacheKey(Tenant tenant, M2MRole role) {
        return tenant.getOrganizationId()
                + "|"
                + role.name();
    }
}
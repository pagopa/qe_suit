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
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class M2Mv3AccessTokenProvider {

    @Value("${interop.auth.oauth.server.sync}")
    private String syncHtu;

    private final M2Mv3ApiClientProvider apiClientProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final DPoPProofService dPoPProofService;

    public M2Mv3AccessTokenContext getOrCreate(
            Tenant tenant,
            M2MRole role)
            throws NoSuchAlgorithmException, JsonProcessingException {

        Client client = apiClientProvider.getOrCreate(
                tenant,
                role
        );

        KeyPair dpopKeyPair = KeyPairUtils.generate(
                KeyAlgorithm.EC
        );

        return issueToken(
                client,
                dpopKeyPair
        );
    }

    private M2Mv3AccessTokenContext issueToken(
            Client client,
            KeyPair dpopKeyPair)
            throws NoSuchAlgorithmException, JsonProcessingException {

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
}
package it.pagopa.kernel.security;

import it.pagopa.application.context.EntityStore;
import it.pagopa.utils.jwt.JwtBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DPoPProofGateway {

    private final DPoPProofService dPoPProofService;
    private final EntityStore entityStore;

    public DPoPProof generateDPoPProof(KeyAlgorithm keyAlgorithm, DPoPProofService.HttpMethod httpMethod, String htu, List<JwtBuilder.JwtClaimOverride> overrides) {
        KeyPair keyPair = KeyPairUtils.generate(keyAlgorithm, 2048);

        String proof = (overrides == null)
                ? dPoPProofService.buildDPoPProof(keyPair, httpMethod, htu)
                : dPoPProofService.buildDPoPProofWithOverrides(keyPair, httpMethod, htu, overrides);

        log.info("Generated DPoP proof: {}", proof);
        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keyPair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        log.info("Public key PEM:\n{}", publicPem);
        log.info("Private key PEM:\n{}", privatePem);

        DPoPProof dpopProof = DPoPProof.builder()
                .key(new Key(keyPair))
                .jwt(proof)
                .build();

        entityStore.upsert(dpopProof);
        return dpopProof;
    }
}

package it.pagopa.interop.common.kernel;

import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.kernel.domain.Key;
import it.pagopa.interop.common.kernel.domain.KeyAlgorithm;
import it.pagopa.interop.common.kernel.security.DPoPProof;
import it.pagopa.interop.common.kernel.security.DPoPProofService;
import it.pagopa.interop.common.kernel.security.KeyPairUtils;
import it.pagopa.interop.common.kernel.utils.jwt.JwtBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DPoPProofGateway {

    private final DPoPProofService dPoPProofService;
    private final EntityStore entityStore;

    public DPoPProof generateDPoPProof(KeyAlgorithm keyAlgorithm, List<JwtBuilder.JwtClaimOverride> overrides) {
        KeyPair keyPair = KeyPairUtils.generate(keyAlgorithm, 2048);
        String proof = (overrides == null)
                ? dPoPProofService.buildProof(keyPair)
                : dPoPProofService.buildProofWithOverrides(keyPair, overrides);

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

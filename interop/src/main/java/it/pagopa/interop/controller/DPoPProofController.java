package it.pagopa.interop.controller;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.context.DPoPProofContext;
import it.pagopa.interop.domain.model.DPoPProof;
import it.pagopa.interop.domain.services.dpop.DPoPProofService;
import it.pagopa.interop.utils.JwtBuilderUtils;
import it.pagopa.interop.utils.KeyPairUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.KeyPair;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DPoPProofController {

    private final DPoPProofService dpopProofService;
    private final DPoPProofContext dpopProofContext;

    @Given("una dpop proof valida generata con una chiave {keyAlgorithm}")
    public void createDPoPProof(KeyPairUtils.KeyAlgorithm keyAlgorithm) {
        KeyPair keyPair = KeyPairUtils.generate(keyAlgorithm, 2048);
        String proof = dpopProofService.buildProof(keyPair);
        dpopProofContext.upsert(new DPoPProof(proof, keyPair));
    }

    @And("una dpop proof generata con una chiave {keyAlgorithm} e:")
    public void createDPoPProofWithKeyTypeAndOverrides(KeyPairUtils.KeyAlgorithm keyAlgorithm, List<JwtBuilderUtils.JwtClaimOverride> overrides) {
        KeyPair keyPair = KeyPairUtils.generate(keyAlgorithm, 2048);
        String proof = dpopProofService.buildProofWithOverrides(keyPair, overrides);
        dpopProofContext.upsert(new DPoPProof(proof, keyPair));
    }
}
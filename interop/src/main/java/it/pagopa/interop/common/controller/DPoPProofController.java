package it.pagopa.interop.common.controller;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.contract.model.shared.DPoPProof;
import it.pagopa.interop.common.service.DPoPProofService;
import it.pagopa.interop.common.utils.JwtBuilderUtils;
import it.pagopa.interop.common.utils.KeyPairUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.KeyPair;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DPoPProofController {

    private final DPoPProofService dpopProofService;
    private final ScenarioContext scenarioContext;

    @Given("una dpop proof valida generata con una chiave {keyAlgorithm}")
    public void createDPoPProof(KeyPairUtils.KeyAlgorithm keyAlgorithm) {
        createAndStoreDPoPProof(keyAlgorithm, null);
    }

    @And("una dpop proof generata con una chiave {keyAlgorithm} e:")
    public void createDPoPProofWithKeyTypeAndOverrides(KeyPairUtils.KeyAlgorithm keyAlgorithm, List<JwtBuilderUtils.JwtClaimOverride> overrides) {
        createAndStoreDPoPProof(keyAlgorithm, overrides);
    }

    private void createAndStoreDPoPProof(KeyPairUtils.KeyAlgorithm keyAlgorithm, List<JwtBuilderUtils.JwtClaimOverride> overrides) {
        KeyPair keyPair = KeyPairUtils.generate(keyAlgorithm, 2048);
        String proof = (overrides == null)
                ? dpopProofService.buildProof(keyPair)
                : dpopProofService.buildProofWithOverrides(keyPair, overrides);

        log.info("Generated DPoP proof: {}", proof);
        scenarioContext.upsert(new DPoPProof(proof, keyPair));
    }
}
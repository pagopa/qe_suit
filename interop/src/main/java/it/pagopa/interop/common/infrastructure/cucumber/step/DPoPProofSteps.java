package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.interop.common.kernel.DPoPProofGateway;
import it.pagopa.interop.common.kernel.domain.KeyAlgorithm;
import it.pagopa.interop.common.kernel.utils.jwt.JwtBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DPoPProofSteps {

    private final DPoPProofGateway dPoPProofGateway;

    @Given("una dpop proof valida generata con una chiave {keyAlgorithm}")
    public void createDPoPProof(KeyAlgorithm keyAlgorithm) {
        dPoPProofGateway.generateDPoPProof(keyAlgorithm, null);
    }

    @And("una dpop proof generata con una chiave {keyAlgorithm} e:")
    public void createDPoPProofWithKeyTypeAndOverrides(KeyAlgorithm keyAlgorithm, List<JwtBuilder.JwtClaimOverride> overrides) {
        dPoPProofGateway.generateDPoPProof(keyAlgorithm, overrides);
    }
}

package it.pagopa.interop.common.infrastructure.cucumber.step;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import it.pagopa.kernel.security.DPoPProofGateway;
import it.pagopa.kernel.security.DPoPProofService;
import it.pagopa.utils.jwt.JwtBuilder;
import it.pagopa.kernel.security.KeyAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DPoPProofSteps {

    private final DPoPProofGateway dPoPProofGateway;

    @Value("${interop.auth.oauth.server.sync}")
    private String syncHtu;

    @Given("una dpop proof valida generata con una chiave {keyAlgorithm}")
    public void createDPoPProof(KeyAlgorithm keyAlgorithm) {
        dPoPProofGateway.generateDPoPProof(keyAlgorithm, DPoPProofService.HttpMethod.POST, syncHtu, null);
    }

    @And("una dpop proof generata con una chiave {keyAlgorithm} e:")
    public void createDPoPProofWithKeyTypeAndOverrides(KeyAlgorithm keyAlgorithm, List<JwtBuilder.JwtClaimOverride> overrides) {
        dPoPProofGateway.generateDPoPProof(keyAlgorithm, DPoPProofService.HttpMethod.POST, syncHtu, overrides);
    }
}

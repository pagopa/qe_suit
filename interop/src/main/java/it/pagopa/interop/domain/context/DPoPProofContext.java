package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.DPoPProof;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class DPoPProofContext extends AbstractContext<DPoPProof> {
}
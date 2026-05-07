package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Purpose;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class PurposeContext extends AbstractContext<Purpose> {
}

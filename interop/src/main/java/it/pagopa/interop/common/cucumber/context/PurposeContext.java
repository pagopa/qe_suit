package it.pagopa.interop.common.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.domain.model.Purpose;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class PurposeContext extends AbstractContext<Purpose> {
}

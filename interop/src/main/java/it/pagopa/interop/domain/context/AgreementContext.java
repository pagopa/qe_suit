package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class AgreementContext extends AbstractContext<Agreement> {
}

package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class BrowserContext extends CurrentUserContext {

}
